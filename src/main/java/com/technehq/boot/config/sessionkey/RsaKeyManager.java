package com.technehq.boot.config.sessionkey;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.technehq.boot.constants.TechneCode;
import com.technehq.boot.exception.TechneException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务端 RSA 密钥管理器，负责：
 * <ul>
 *     <li>对外提供 RSA 公钥（{@link #getPublicKeyBase64()}），供客户端加密 sessionKey / token；</li>
 *     <li>用 RSA 私钥解密客户端传来的 {@code sessionKey}（得到本次 AES 密钥）；</li>
 *     <li>用 RSA 私钥解密并校验 {@code token}。</li>
 * </ul>
 * 密钥来源二选一：
 * <ul>
 *     <li><b>静态固定</b>：配置了 {@code privateKeyBase64} + {@code publicKeyBase64} 时使用，不轮换，
 *     多实例共享同一把密钥，天然集群安全；</li>
 *     <li><b>自动生成 + 轮换</b>：未固定时生成 2048 位密钥，每 {@code keyRotationHours} 小时轮换，
 *     保留上一把私钥 {@code keyGraceHours} 小时作为宽限。</li>
 * </ul>
 *
 * @author Techne SDK
 */
@Slf4j
public class RsaKeyManager {

    private final SessionKeyEncryptProperties properties;
    private final String agreedToken;

    private volatile String currentPrivateKeyBase64;
    private volatile String currentPublicKeyBase64;
    private volatile String previousPrivateKeyBase64;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "rsa-key-rotation");
        t.setDaemon(true);
        return t;
    });

    public RsaKeyManager(SessionKeyEncryptProperties properties) {
        this.properties = properties;
        this.agreedToken = properties.getAgreedToken();
        if (StringUtils.hasText(properties.getPrivateKeyBase64()) && StringUtils.hasText(properties.getPublicKeyBase64())) {
            this.currentPrivateKeyBase64 = properties.getPrivateKeyBase64();
            this.currentPublicKeyBase64 = properties.getPublicKeyBase64();
            log.info("RsaKeyManager initialized with pinned static RSA key (rotation disabled).");
        } else {
            KeyPair pair = SecureUtil.generateKeyPair("RSA", 2048);
            RSA rsa = new RSA(pair.getPrivate().getEncoded(), pair.getPublic().getEncoded());
            this.currentPrivateKeyBase64 = rsa.getPrivateKeyBase64();
            this.currentPublicKeyBase64 = rsa.getPublicKeyBase64();
            log.info("RsaKeyManager initialized with generated RSA key (rotation every {}h, grace {}h).",
                    properties.getKeyRotationHours(), properties.getKeyGraceHours());
            scheduleRotation();
        }
    }

    /**
     * 获取当前 RSA 公钥（Base64），客户端用它加密 sessionKey / token。
     *
     * @return 公钥 Base64
     */
    public String getPublicKeyBase64() {
        return currentPublicKeyBase64;
    }

    /**
     * 用 RSA 私钥解密 sessionKey，得到本次请求使用的 AES 密钥（32 字节）。
     * 先试当前私钥，再试上一把私钥（宽限期内）。
     *
     * @param sessionKeyBase64 客户端传来的 RSA 加密后的 AES 密钥（Base64）
     * @return AES 密钥字节数组
     */
    public byte[] resolveAesKey(String sessionKeyBase64) {
        byte[] key = tryDecrypt(sessionKeyBase64, currentPrivateKeyBase64);
        if (key != null) {
            return key;
        }
        if (previousPrivateKeyBase64 != null) {
            key = tryDecrypt(sessionKeyBase64, previousPrivateKeyBase64);
            if (key != null) {
                return key;
            }
        }
        throw new TechneException(TechneCode.SESSION_KEY_INVALID);
    }

    /**
     * 用 RSA 私钥解密 token 并与约定字符串比对，不一致则抛 {@link TechneCode#TOKEN_INVALID}。
     *
     * @param tokenBase64 客户端传来的 RSA 加密后的约定字符串（Base64）
     */
    public void verifyToken(String tokenBase64) {
        if (!StringUtils.hasText(agreedToken)) {
            throw new TechneException(TechneCode.TOKEN_INVALID);
        }
        byte[] decoded = tryDecrypt(tokenBase64, currentPrivateKeyBase64);
        if (decoded == null && previousPrivateKeyBase64 != null) {
            decoded = tryDecrypt(tokenBase64, previousPrivateKeyBase64);
        }
        if (decoded == null || !new String(decoded, StandardCharsets.UTF_8).equals(agreedToken)) {
            throw new TechneException(TechneCode.TOKEN_INVALID);
        }
    }

    private byte[] tryDecrypt(String cipherBase64, String privateKeyBase64) {
        try {
            RSA rsa = new RSA(privateKeyBase64, null);
            return rsa.decrypt(cipherBase64, KeyType.PrivateKey);
        } catch (Exception e) {
            return null;
        }
    }

    private void scheduleRotation() {
        if (properties.getKeyRotationHours() <= 0) {
            return;
        }
        scheduler.scheduleAtFixedRate(this::rotate,
                properties.getKeyRotationHours(), properties.getKeyRotationHours(), TimeUnit.HOURS);
    }

    private synchronized void rotate() {
        try {
            KeyPair pair = SecureUtil.generateKeyPair("RSA", 2048);
            RSA rsa = new RSA(pair.getPrivate().getEncoded(), pair.getPublic().getEncoded());
            this.previousPrivateKeyBase64 = this.currentPrivateKeyBase64;
            this.currentPrivateKeyBase64 = rsa.getPrivateKeyBase64();
            this.currentPublicKeyBase64 = rsa.getPublicKeyBase64();
            log.info("RsaKeyManager rotated RSA key.");
        } catch (Exception e) {
            log.error("RsaKeyManager rotation failed", e);
        }
    }

    /**
     * 关闭轮换调度线程。
     */
    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
