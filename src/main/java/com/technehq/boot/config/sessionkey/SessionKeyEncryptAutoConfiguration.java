package com.technehq.boot.config.sessionkey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 会话密钥加解密自动配置。
 * <p>支撑 Bean（密钥管理器、字段加解密器、配置属性）始终装配；
 * 是否对某个接口生效由 {@code @SessionKeyEncrypt} 注解 + {@code techne.session-key.enabled} 共同决定。
 * 由 mica-auto 注解处理器在编译期扫描 {@link AutoConfiguration} 自动注册。</p>
 *
 * @author Techne SDK
 */
@AutoConfiguration(value = "sessionKeyEncryptAutoConfiguration")
@EnableConfigurationProperties(SessionKeyEncryptProperties.class)
public class SessionKeyEncryptAutoConfiguration {

    @Bean
    public RsaKeyManager rsaKeyManager(SessionKeyEncryptProperties properties) {
        return new RsaKeyManager(properties);
    }

    @Bean
    public AesGcmFieldCryptor aesGcmFieldCryptor() {
        return new AesGcmFieldCryptor();
    }
}
