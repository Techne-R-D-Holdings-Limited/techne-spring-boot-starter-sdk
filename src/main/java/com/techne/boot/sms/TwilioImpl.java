package com.techne.boot.sms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.techne.boot.config.properties.TwilioProperties;
import com.techne.boot.util.AwsSecretsManagerUtil;
import com.techne.boot.util.GsonUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import net.dreamlu.mica.auto.annotation.AutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TwilioImpl
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoService(SmsInterface.class)
public class TwilioImpl implements SmsInterface {

    /**
     * 此处不可以使用@Slf4j注解，否则会无法通过ServiceLoaderUtil.loadFirstAvailable获取到
     */
    private static final Logger log = LoggerFactory.getLogger(TwilioImpl.class);

    static String messagingServiceSid;

    /**
     * 字母发件人 ID（Alphanumeric Sender ID）
     */
    static String from;

    /**
     * 构造函数，从 AWS Secrets / yml 初始化 Twilio 凭据、messagingServiceSid 与字母发件人 ID
     * Constructor, initializes Twilio credentials, messagingServiceSid and the alphanumeric sender ID from AWS Secrets / yml
     *
     * @author Lil' Doe
     * 2026/6/16 16:34
     */
    public TwilioImpl() {
        TwilioProperties twilio = SpringUtil.getBean(TwilioProperties.class);
        JsonNode jsonNode = AwsSecretsManagerUtil.getSecret();
        String accountSid = StrUtil.blankToDefault(jsonNode.path(twilio.getAccountSidSecrets()).asText(), twilio.getAccountSid());
        String authToken = StrUtil.blankToDefault(jsonNode.path(twilio.getAuthTokenSecrets()).asText(), twilio.getAuthToken());
        Twilio.init(accountSid, authToken);
        messagingServiceSid = StrUtil.blankToDefault(jsonNode.path(twilio.getMessagingServiceSidSecrets()).asText(), twilio.getMessagingServiceSid());
        from = StrUtil.blankToDefault(jsonNode.path(twilio.getFromSecrets()).asText(), twilio.getFrom());
    }

    /**
     * 根据配置自动使用对应的短信平台发送短信<br/>
     * yml配置twilio或smsBroadcast
     *
     * @param send        是否发送
     * @param phoneNumber 带区号的手机号码
     * @param message     消息内容
     */
    @Override
    public void sendMessage(boolean send, String phoneNumber, String message) {
        if (send) {
            Message msg = Message.creator(
                                         new PhoneNumber(phoneNumber),
                                         messagingServiceSid,
                                         message)
                                 .create();
            log.info("TwilioImpl.sendMessage --> msg: {}", GsonUtil.toJson(msg));
        }
    }

    /**
     * 使用字母发件人 ID（Alphanumeric Sender ID）发送短信，不使用 messagingServiceSid；发件人取自配置的 from（techne.twilio.from / fromSecrets）
     * Send SMS using an alphanumeric sender ID instead of messagingServiceSid; the sender is taken from the configured from (techne.twilio.from / fromSecrets)
     *
     * @param send        是否发送 / whether to actually send
     * @param phoneNumber 带区号的手机号码 / phone number with country code
     * @param message     消息内容 / message content
     * @author Lil' Doe
     * 2026/6/16 16:34
     */
    @Override
    public void sendMessageBySenderId(boolean send, String phoneNumber, String message) {
        if (send) {
            Message msg = Message.creator(
                                         new PhoneNumber(phoneNumber),
                                         new PhoneNumber(from),
                                         message)
                                 .create();
            log.info("TwilioImpl.sendMessageBySenderId --> msg: {}", GsonUtil.toJson(msg));
        }
    }

}
