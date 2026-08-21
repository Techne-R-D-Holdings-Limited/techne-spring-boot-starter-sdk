package com.technehq.boot.config.sessionkey;

/**
 * 会话密钥上下文，在同一请求线程内传递本次解析出的 AES 会话密钥，
 * 供请求解密 Advice 写入、响应加密 Advice 读取。
 *
 * @author Techne SDK
 */
public final class SessionKeyContext {

    private static final ThreadLocal<byte[]> AES_KEY = new ThreadLocal<>();

    private SessionKeyContext() {
    }

    /**
     * 写入本次请求的 AES 会话密钥。
     *
     * @param aesKey AES 密钥字节数组
     */
    public static void setAesKey(byte[] aesKey) {
        AES_KEY.set(aesKey);
    }

    /**
     * 读取本次请求的 AES 会话密钥。
     *
     * @return AES 密钥字节数组，未设置时为 null
     */
    public static byte[] getAesKey() {
        return AES_KEY.get();
    }

    /**
     * 清除本次请求的 AES 会话密钥，应在响应写回后调用。
     */
    public static void clear() {
        AES_KEY.remove();
    }
}
