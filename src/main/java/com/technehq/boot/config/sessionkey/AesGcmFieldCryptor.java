package com.technehq.boot.config.sessionkey;

import com.technehq.boot.constants.TechneCode;
import com.technehq.boot.exception.TechneException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 字段级 AES/GCM/NoPadding 加解密。
 * <p>密文格式（与移动端约定一致）：
 * {@code Base64( IV(12B) || cipherText || GCM Tag(16B) )}。</p>
 * <ul>
 *     <li>IV：随机 12 字节；</li>
 *     <li>GCM Tag：128 位（16 字节），由 JCE 默认附加在密文尾部；</li>
 *     <li>AES 密钥：客户端生成的会话密钥（通常 32 字节 → AES-256）。</li>
 * </ul>
 *
 * @author Techne SDK
 */
@Slf4j
public class AesGcmFieldCryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    /**
     * 加密单个字段明文。
     *
     * @param plainText 字段明文
     * @param aesKey    本次请求的 AES 会话密钥
     * @return 密文 Base64（IV(12B) || cipherText || tag(16B)）
     */
    public String encryptField(String plainText, byte[] aesKey) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] out = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(cipherText, 0, out, iv.length, cipherText.length);
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new TechneException(TechneCode.SESSION_KEY_INVALID);
        }
    }

    /**
     * 解密单个字段密文。
     *
     * @param cipherBase64 密文 Base64（IV(12B) || cipherText || tag(16B)）
     * @param aesKey       本次请求的 AES 会话密钥
     * @return 字段明文
     */
    public String decryptField(String cipherBase64, byte[] aesKey) {
        try {
            byte[] raw = Base64.getDecoder().decode(cipherBase64);
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(raw, 0, iv, 0, IV_LENGTH);
            byte[] cipherText = new byte[raw.length - IV_LENGTH];
            System.arraycopy(raw, IV_LENGTH, cipherText, 0, cipherText.length);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new TechneException(TechneCode.SESSION_KEY_INVALID);
        }
    }
}
