package com.techne.boot.config.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TwilioProperties
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration(value = "twilioProperties")
@ConfigurationProperties(prefix = "techne.twilio")
public class TwilioProperties {

    /**
     * accountSid
     */
    private String accountSid;

    /**
     * authToken
     */
    private String authToken;

    /**
     * messagingServiceSid
     */
    private String messagingServiceSid;

    /**
     * 字母发件人 ID（Alphanumeric Sender ID），使用此方式发送时不需要 messagingServiceSid
     */
    private String from;

    /**
     * AWSSecrets里面的key，此优先级高于accountSid
     */
    private String accountSidSecrets;

    /**
     * AWSSecrets里面的key，此优先级高于authToken
     */
    private String authTokenSecrets;

    /**
     * AWSSecrets里面的key，此优先级高于messagingServiceSid
     */
    private String messagingServiceSidSecrets;

    /**
     * AWSSecrets里面的key，此优先级高于from
     */
    private String fromSecrets;

}
