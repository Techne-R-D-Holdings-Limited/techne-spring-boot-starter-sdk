package com.technehq.boot.sms;

/**
 * SmsUtil
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class SmsUtil {

    /**
     * 根据配置自动使用对应的短信平台发送短信<br/>
     * yml配置twilio或smsBroadcast
     *
     * @param send        是否发送
     * @param phoneNumber 带区号的手机号码
     * @param message     消息内容
     */
    public static void sendMessage(boolean send, String phoneNumber, String message) {
        SmsFactory.get().sendMessage(send, phoneNumber, message);
    }

    /**
     * 使用字母发件人 ID（Alphanumeric Sender ID）发送短信，不使用 messagingServiceSid
     * Send SMS using an alphanumeric sender ID instead of messagingServiceSid
     *
     * @param send        是否发送 / whether to actually send
     * @param phoneNumber 带区号的手机号码 / phone number with country code
     * @param message     消息内容 / message content
     * @author Lil' Doe
     * 2026/6/16 16:34
     */
    public static void sendMessageBySenderId(boolean send, String phoneNumber, String message) {
        SmsFactory.get().sendMessageBySenderId(send, phoneNumber, message);
    }

}
