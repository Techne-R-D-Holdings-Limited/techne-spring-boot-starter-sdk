package com.technehq.boot.config.sessionkey;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technehq.boot.annotation.SessionKeyEncrypt;
import com.technehq.boot.pojo.basic.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 响应加密 Advice：当 Controller 方法标记 {@link SessionKeyEncrypt} 时生效。
 * <p>读取 {@link SessionKeyContext} 中本次请求的 AES 密钥，
 * 将 {@code ResponseData.data} 的<b>每一个顶层字段</b>（以及 List 的每个元素）做 AES/GCM 加密。</p>
 * <p>加密后 {@code data} 的结构保持不变（字段名不变，字段值变为密文），
 * 客户端逐字段解密即可。处理结束后清除线程内的 AES 密钥。</p>
 *
 * @author Techne SDK
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class SessionKeyEncryptResponseBodyAdvice implements ResponseBodyAdvice<ResponseData<Object>> {

    private final ObjectMapper objectMapper;

    private final AesGcmFieldCryptor aesGcmFieldCryptor;

    private final SessionKeyEncryptProperties properties;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (!properties.isEnabled()) {
            return false;
        }
        SessionKeyEncrypt methodAnnotation = returnType.getMethodAnnotation(SessionKeyEncrypt.class);
        if (Objects.nonNull(methodAnnotation)) {
            return true;
        }
        SessionKeyEncrypt classAnnotation = AnnotationUtil.getAnnotation(returnType.getContainingClass(), SessionKeyEncrypt.class);
        return Objects.nonNull(classAnnotation);
    }

    @Override
    @SneakyThrows
    @Nullable
    public ResponseData<Object> beforeBodyWrite(@Nullable ResponseData<Object> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            if (ObjUtil.isNull(body)) {
                return body;
            }
            byte[] aesKey = SessionKeyContext.getAesKey();
            if (aesKey == null) {
                return body;
            }
            Object data = body.getData();
            if (ObjUtil.isNull(data)) {
                return body;
            }
            String json = objectMapper.writeValueAsString(data);
            Object parsed = objectMapper.readValue(json, Object.class);
            Object encrypted = encryptValue(parsed, aesKey);
            body.setData(encrypted);
            return body;
        } finally {
            SessionKeyContext.clear();
        }
    }

    private Object encryptValue(Object value, byte[] aesKey) {
        if (value instanceof Map) {
            Map<String, Object> src = (Map<String, Object>) value;
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : src.entrySet()) {
                out.put(entry.getKey(), encryptSingle(entry.getValue(), aesKey));
            }
            return out;
        }
        if (value instanceof List) {
            List<Object> src = (List<Object>) value;
            List<Object> out = new ArrayList<>(src.size());
            for (Object item : src) {
                out.add(encryptSingle(item, aesKey));
            }
            return out;
        }
        return encryptSingle(value, aesKey);
    }

    private Object encryptSingle(Object value, byte[] aesKey) {
        if (value == null) {
            return null;
        }
        String json = (value instanceof String) ? (String) value : writeJson(value);
        return aesGcmFieldCryptor.encryptField(json, aesKey);
    }

    @SneakyThrows
    private String writeJson(Object value) {
        return objectMapper.writeValueAsString(value);
    }
}
