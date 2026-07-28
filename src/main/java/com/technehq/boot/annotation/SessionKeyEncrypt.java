package com.technehq.boot.annotation;

import java.lang.annotation.*;

/**
 * <p>接口字段级会话密钥加解密。</p>
 * <p>标注在 Controller 方法或类上后，该接口走「每请求一个 AES 会话密钥 + 字段级 AES/GCM」机制：</p>
 * <ul>
 *     <li><b>入参</b>：要求客户端发送信封
 *     {@code { "sessionKey": "...", "token": "...", "payload": { ...加密字段... } }，
 *     服务端用服务端 RSA 私钥解密 {@code sessionKey} 得到本次 AES 密钥，
 *     校验 {@code token}，再用该 AES 密钥解密 {@code payload} 的每一个字段。</li>
 *     <li><b>出参</b>：将 {@code ResponseData.data} 的<b>每一个顶层字段</b>用本次 AES 密钥做 AES/GCM 加密。</li>
 * </ul>
 * <p>服务端 RSA 公钥通过 {@code RsaKeyManager#getPublicKeyBase64()} 提供给客户端，
 * 由业务方自行暴露一个获取公钥的接口（参考约定文档）。</p>
 *
 * @author Techne SDK
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionKeyEncrypt {
}
