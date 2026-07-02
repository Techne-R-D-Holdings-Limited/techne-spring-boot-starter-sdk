package com.techne.boot.pojo.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techne.boot.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * AWS Secrets Manager 固定密钥信息。
 * AWS Secrets Manager fixed key information.
 *
 * @author Lil' Doe
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "AWS Secrets Manager 固定密钥信息")
@Accessors(chain = true)
public class SecretInfoBO extends AbstractBasicSerializable {

    /**
     * AWS S3 访问密钥 ID。
     * AWS S3 access key ID.
     */
    @JsonProperty("AWS-S3-Access-key-ID")
    @Schema(description = "AWS S3 access key ID")
    private String awsS3AccessKeyId;

    /**
     * AWS S3 访问密钥 Secret。
     * AWS S3 secret access key.
     */
    @JsonProperty("AWS-S3-Secret-access-key")
    @Schema(description = "AWS S3 secret access key")
    private String awsS3SecretAccessKey;

    /**
     * Stripe 正式环境密钥。
     * Stripe live secret key.
     */
    @JsonProperty("Stripe-Live-key")
    @Schema(description = "Stripe live secret key")
    private String stripeLiveKey;

    /**
     * Stripe 测试环境密钥。
     * Stripe test secret key.
     */
    @JsonProperty("Stripe-Test-key")
    @Schema(description = "Stripe test secret key")
    private String stripeTestKey;

    /**
     * Stripe 正式环境可发布密钥。
     * Stripe live publishable key.
     */
    @JsonProperty("Stripe-Live-PK-key")
    @Schema(description = "Stripe live publishable key")
    private String stripeLivePkKey;

    /**
     * Stripe 测试环境可发布密钥。
     * Stripe test publishable key.
     */
    @JsonProperty("Stripe-Test-PK-key")
    @Schema(description = "Stripe test publishable key")
    private String stripeTestPkKey;

    /**
     * Stripe 正式环境跳转地址。
     * Stripe live redirect URI.
     */
    @JsonProperty("Stripe-Live-Redirect-URI")
    @Schema(description = "Stripe live redirect URI")
    private String stripeLiveRedirectUri;

    /**
     * Stripe 测试环境跳转地址。
     * Stripe test redirect URI.
     */
    @JsonProperty("Stripe-Test-Redirect-URI")
    @Schema(description = "Stripe test redirect URI")
    private String stripeTestRedirectUri;

    /**
     * Google Maps Android 密钥。
     * Google Maps Android key.
     */
    @JsonProperty("Google-Maps-Android")
    @Schema(description = "Google Maps Android key")
    private String googleMapsAndroid;

    /**
     * Google Maps iOS 密钥。
     * Google Maps iOS key.
     */
    @JsonProperty("Google-Maps-IOS")
    @Schema(description = "Google Maps iOS key")
    private String googleMapsIos;

    /**
     * Google Maps Web 密钥。
     * Google Maps web key.
     */
    @JsonProperty("Google-Maps-Web")
    @Schema(description = "Google Maps web key")
    private String googleMapsWeb;

    /**
     * Mailchimp API 密钥。
     * Mailchimp API key.
     */
    @JsonProperty("Mailchimp-API-key")
    @Schema(description = "Mailchimp API key")
    private String mailchimpApiKey;

    /**
     * Mailchimp 发件邮箱。
     * Mailchimp outbox.
     */
    @JsonProperty("Mailchimp-Outbox")
    @Schema(description = "Mailchimp outbox")
    private String mailchimpOutbox;

    /**
     * Mailchimp 发件人名称。
     * Mailchimp from name.
     */
    @JsonProperty("Mailchimp-From-Name")
    @Schema(description = "Mailchimp from name")
    private String mailchimpFromName;

    /**
     * Sendbird 应用 ID。
     * Sendbird application ID.
     */
    @JsonProperty("Sendbird-APP-ID")
    @Schema(description = "Sendbird application ID")
    private String sendbirdAppId;

    /**
     * Sendbird API Token。
     * Sendbird API token.
     */
    @JsonProperty("Sendbird-API-Token")
    @Schema(description = "Sendbird API token")
    private String sendbirdApiToken;

    /**
     * Facebook 应用 ID。
     * Facebook application ID.
     */
    @JsonProperty("Facebook-APP-ID")
    @Schema(description = "Facebook application ID")
    private String facebookAppId;

    /**
     * Facebook 应用密钥。
     * Facebook application secret.
     */
    @JsonProperty("Facebook-APP-Secret")
    @Schema(description = "Facebook application secret")
    private String facebookAppSecret;

    /**
     * Twilio 账户 SID。
     * Twilio account SID.
     */
    @JsonProperty("Twilio-Account-SID")
    @Schema(description = "Twilio account SID")
    private String twilioAccountSid;

    /**
     * Twilio 认证 Token。
     * Twilio auth token.
     */
    @JsonProperty("Twilio-Auth-Token")
    @Schema(description = "Twilio auth token")
    private String twilioAuthToken;

    /**
     * Twilio 服务 ID。
     * Twilio service ID.
     */
    @JsonProperty("Twilio-Service-ID")
    @Schema(description = "Twilio service ID")
    private String twilioServiceId;

    /**
     * Microsoft 应用客户端 ID。
     * Microsoft application client ID.
     */
    @JsonProperty("MS-APP-Client-ID")
    @Schema(description = "Microsoft application client ID")
    private String msAppClientId;

    /**
     * Microsoft 应用密钥 ID。
     * Microsoft application secret ID.
     */
    @JsonProperty("MS-APP-Secret-ID")
    @Schema(description = "Microsoft application secret ID")
    private String msAppSecretId;

    /**
     * Microsoft 应用服务端跳转地址。
     * Microsoft application service redirect URI.
     */
    @JsonProperty("MS-APP-Service-Redirect-URI")
    @Schema(description = "Microsoft application service redirect URI")
    private String msAppServiceRedirectUri;

    /**
     * Microsoft 应用 Web 跳转地址。
     * Microsoft application web redirect URI.
     */
    @JsonProperty("MS-APP-Web-Redirect-URI")
    @Schema(description = "Microsoft application web redirect URI")
    private String msAppWebRedirectUri;

}
