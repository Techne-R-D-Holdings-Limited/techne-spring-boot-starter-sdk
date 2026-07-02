package com.techne.boot.sms;

/**
 * SmsInterface
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface SmsInterface {

    /**
     * 根据配置自动使用对应的短信平台发送短信<br/>
     * yml配置twilio或smsBroadcast
     *
     * @param send        是否发送
     * @param phoneNumber 带区号的手机号码
     * @param message     消息内容
     */
    void sendMessage(boolean send, String phoneNumber, String message);

    /**
     * 使用字母发件人 ID（Alphanumeric Sender ID）发送短信，不使用 messagingServiceSid；默认回退到 {@link #sendMessage}，由具体实现按需重写
     * Send SMS using an alphanumeric sender ID instead of messagingServiceSid; falls back to {@link #sendMessage} by default, to be overridden by implementations as needed
     *
     * @param send        是否发送 / whether to actually send
     * @param phoneNumber 带区号的手机号码 / phone number with country code
     * @param message     消息内容 / message content
     * @author Lil' Doe
     * 2026/6/16 16:34
     */
    default void sendMessageBySenderId(boolean send, String phoneNumber, String message) {
        sendMessage(send, phoneNumber, message);
    }

}
