package com.technehq.boot.config.sessionkey;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 会话密钥加解密配置，前缀 {@code techne.session-key}。
 *
 * @author Techne SDK
 */
@Data
@ConfigurationProperties(prefix = "techne.session-key")
public class SessionKeyEncryptProperties {

    /**
     * 是否启用会话密钥加解密，默认关闭。
     */
    private boolean enabled = false;

    /**
     * 约定的身份字符串，客户端用服务端 RSA 公钥加密后放在请求信封的 token 字段。
     * 启用时必填，缺失会导致所有被 {@code @SessionKeyEncrypt} 标记的接口校验失败。
     */
    private String agreedToken;

    /**
     * RSA 密钥轮换周期（小时），默认 24。
     * 轮换后保留上一把私钥 {@code key-grace-hours} 小时，供仍持有旧公钥的客户端平滑过渡。
     * 仅在未固定静态密钥时生效。
     */
    private int keyRotationHours = 24;

    /**
     * 上一把 RSA 私钥的宽限保留时间（小时），默认 1。
     * 应不小于客户端对公钥的缓存时间。
     */
    private int keyGraceHours = 1;

    /**
     * 固定静态 RSA 私钥（Base64）。与 {@code publicKeyBase64} 同时配置时生效，
     * 此时不轮换，所有实例使用同一把密钥（多实例部署安全）。
     */
    private String privateKeyBase64;

    /**
     * 固定静态 RSA 公钥（Base64）。与 {@code privateKeyBase64} 同时配置时生效。
     */
    private String publicKeyBase64;
}
