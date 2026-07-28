package com.technehq.boot.config.sessionkey;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technehq.boot.annotation.SessionKeyEncrypt;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 请求解密 Advice：当 Controller 方法标记 {@link SessionKeyEncrypt} 时生效。
 * <p>读取信封 {@code {sessionKey, token, payload}}：
 * 用服务端 RSA 私钥解密 {@code sessionKey} 得到本次 AES 密钥，
 * 校验 {@code token}，再用 AES 密钥解密 {@code payload} 的每一个字段，
 * 最后把解密后的 payload 作为新的请求体交给 Spring 反序列化到 {@code @RequestBody} 参数。</p>
 *
 * @author Techne SDK
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class SessionKeyEncryptRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private final ObjectMapper objectMapper;

    private final RsaKeyManager rsaKeyManager;

    private final AesGcmFieldCryptor aesGcmFieldCryptor;

    private final SessionKeyEncryptProperties properties;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (!properties.isEnabled()) {
            return false;
        }
        SessionKeyEncrypt methodAnnotation = methodParameter.getMethodAnnotation(SessionKeyEncrypt.class);
        if (Objects.nonNull(methodAnnotation)) {
            return true;
        }
        SessionKeyEncrypt classAnnotation = AnnotationUtil.getAnnotation(methodParameter.getContainingClass(), SessionKeyEncrypt.class);
        return Objects.nonNull(classAnnotation);
    }

    @Override
    @SneakyThrows
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        String raw = IoUtil.readUtf8(inputMessage.getBody());
        Map<String, Object> envelope = objectMapper.readValue(raw, new TypeReference<LinkedHashMap<String, Object>>() {});
        String sessionKey = (String) envelope.get("sessionKey");
        String token = (String) envelope.get("token");
        byte[] aesKey = rsaKeyManager.resolveAesKey(sessionKey);
        rsaKeyManager.verifyToken(token);

        Map<String, Object> decrypted = new LinkedHashMap<>();
        Object payload = envelope.get("payload");
        if (payload instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) payload).entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String cipher) {
                    String plain = aesGcmFieldCryptor.decryptField(cipher, aesKey);
                    decrypted.put(entry.getKey(), toTypedValue(plain));
                } else {
                    decrypted.put(entry.getKey(), value);
                }
            }
        }
        SessionKeyContext.setAesKey(aesKey);
        final byte[] bodyBytes = objectMapper.writeValueAsBytes(decrypted);
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(bodyBytes);
            }

            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
        };
    }

    private Object toTypedValue(String plain) {
        String trimmed = plain.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            try {
                return objectMapper.readValue(trimmed, Object.class);
            } catch (Exception ignored) {
                // 不是合法 JSON，按原字符串返回
            }
        }
        return plain;
    }
}
