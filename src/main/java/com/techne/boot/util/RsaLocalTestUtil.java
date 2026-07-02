package com.techne.boot.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.RSA;
import com.techne.boot.config.StaticConfig;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

/**
 * RSA 本地测试工具类。
 * RSA local test helper.
 *
 * @author Lil' Doe
 */
public final class RsaLocalTestUtil {

    /**
     * 禁止实例化本地测试 RSA 工具类。
     * Prevents instantiation of the local test RSA utility class.
     *
     * @author Lil' Doe
     * 2026/7/1 16:08
     */
    private RsaLocalTestUtil() {
    }

    /**
     * 根据当前项目名生成本地测试备用 RSA 实例，若无法获取项目名则抛出异常。
     * Generates a local-test fallback RSA instance from the current project name, throwing an exception if the project name cannot be resolved.
     *
     * @return RSA 本地测试备用 RSA 实例 / local-test fallback RSA instance
     * @author Lil' Doe
     * 2026/7/1 16:08
     */
    public static RSA generate() {
        String projectName = getProjectName();
        KeyPair keyPair = generateKeyPairByProjectName(projectName);
        return SecureUtil.rsa(keyPair.getPrivate().getEncoded(), keyPair.getPublic().getEncoded());
    }

    /**
     * 根据当前项目名生成本地测试备用 Base64 公私钥，若无法获取项目名则抛出异常。
     * Generates local-test fallback Base64 private and public keys from the current project name, throwing an exception if the project name cannot be resolved.
     *
     * @return RsaKeyPairBase64 Base64 公私钥 / Base64 private and public keys
     * @author Lil' Doe
     * 2026/7/1 16:08
     */
    public static RsaKeyPairBase64 generateKeyPairBase64() {
        RSA rsa = generate();
        return new RsaKeyPairBase64(rsa.getPrivateKeyBase64(), rsa.getPublicKeyBase64());
    }

    /**
     * 生成指定项目名对应的本地测试备用 RSA 密钥对，若项目名为空则抛出异常。
     * Generates a local-test fallback RSA key pair for the specified project name, throwing an exception when the project name is blank.
     *
     * @param projectName 项目名 / project name
     * @return KeyPair RSA 密钥对 / RSA key pair
     * @author Lil' Doe
     * 2026/7/1 16:08
     */
    private static KeyPair generateKeyPairByProjectName(String projectName) {
        Assert.notBlank(projectName, "projectName must not be blank");
        return SecureUtil.generateKeyPair(
                AsymmetricAlgorithm.RSA.getValue(),
                SecureUtil.DEFAULT_KEY_SIZE,
                projectName.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 获取当前项目名，优先使用 techne.project-name，若为空则使用 spring.application.name，若仍为空则返回 null。
     * Resolves the current project name, preferring techne.project-name, falling back to spring.application.name, and returning null if neither exists.
     *
     * @return String 当前项目名 / current project name
     * @author Lil' Doe
     * 2026/7/1 16:08
     */
    private static String getProjectName() {
        String projectName = StaticConfig.techneProperties != null ? StaticConfig.techneProperties.getProjectName() : null;
        return StrUtil.blankToDefault(projectName, StaticConfig.applicationName);
    }

    public static final class RsaKeyPairBase64 {

        private final String privateKeyBase64;

        private final String publicKeyBase64;

        /**
         * 创建 Base64 公私钥值对象。
         * Creates a Base64 private and public key value object.
         *
         * @param privateKeyBase64 Base64 私钥 / Base64 private key
         * @param publicKeyBase64  Base64 公钥 / Base64 public key
         * @author Lil' Doe
         * 2026/7/1 16:08
         */
        private RsaKeyPairBase64(String privateKeyBase64, String publicKeyBase64) {
            this.privateKeyBase64 = privateKeyBase64;
            this.publicKeyBase64 = publicKeyBase64;
        }

        /**
         * 获取 Base64 私钥。
         * Gets the Base64 private key.
         *
         * @return String Base64 私钥 / Base64 private key
         * @author Lil' Doe
         * 2026/7/1 16:08
         */
        public String getPrivateKeyBase64() {
            return privateKeyBase64;
        }

        /**
         * 获取 Base64 公钥。
         * Gets the Base64 public key.
         *
         * @return String Base64 公钥 / Base64 public key
         * @author Lil' Doe
         * 2026/7/1 16:08
         */
        public String getPublicKeyBase64() {
            return publicKeyBase64;
        }
    }
}
