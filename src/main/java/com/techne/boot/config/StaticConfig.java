package com.techne.boot.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.useragent.Platform;
import com.techne.boot.config.properties.TechneProperties;
import com.techne.boot.constants.TechneConstants;
import com.techne.boot.util.TechneUtil;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.auto.annotation.AutoRunListener;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Locale;

/**
 * StaticConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@AutoRunListener
public class StaticConfig implements SpringApplicationRunListener {

    /**
     * 自定义额外属性值
     */
    public static TechneProperties techneProperties;

    /**
     * 模块应用名称
     */
    public static String applicationName;

    /**
     * 运行环境
     */
    public static String active;

    /**
     * RSA
     */
    public static RSA rsa;

    /**
     * AES
     */
    public static AES aes;

    /**
     * 构造方法，需要提供这个构造方法，否则不会被 Spring 加载
     *
     * @param application application
     * @param args        args
     */
    public StaticConfig(SpringApplication application, String[] args) {
        if (application.getListeners().stream().noneMatch(item -> item instanceof ApplicationPidFileWriter)) {
            // 添加应用程序PID文件监听器
            application.addListeners(new ApplicationPidFileWriter());
        }
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        Binder binder = Binder.get(environment);
        TechneProperties techneProperties = binder.bind("techne", TechneProperties.class).orElse(new TechneProperties());
        String applicationName = environment.getProperty("spring.application.name");
        String active = environment.getProperty("spring.profiles.active");
        StaticConfig.techneProperties = techneProperties;
        StaticConfig.applicationName = applicationName;
        StaticConfig.active = active;
        String aesKey = techneProperties.getAesKey();
        if (StrUtil.isBlank(aesKey)) {
            // 如果没有指定aes的key，则使用默认的aesKey对项目名称+环境进行加密后截取前16位得到新的aesKey
            String data = StrUtil.concat(true, StrUtil.blankToDefault(techneProperties.getProjectName(), applicationName), StrUtil.DASHED, active);
            aesKey = StrUtil.subPre(
                    SecureUtil.aes("NT0Z1y2X725C6b7A".getBytes(StandardCharsets.UTF_8)).encryptBase64(data),
                    16
            );
        }
        StaticConfig.aes = SecureUtil.aes(aesKey.getBytes(StandardCharsets.UTF_8));
        // 将Android平板平台类型添加到Hutool的Platform静态变量中。
        Platform.mobilePlatforms.add(4, TechneConstants.ANDROID_TABLET);
        Platform.platforms.add(4, TechneConstants.ANDROID_TABLET);
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String applicationName = environment.getProperty("spring.application.name");
        String javaVendor = environment.getProperty("java.vendor");
        String javaVersion = environment.getProperty("java.version");
        String serverPort = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path");
        log.info("""
                         
                          _____  _____  ____  _   _  _   _  _____
                         |_   _|| ____|/ ___|| | | || \\ | || ____|
                           | |  |  _| | |    | |_| ||  \\| ||  _|
                           | |  | |___| |___ |  _  || |\\  || |___
                           |_|  |_____|\\____||_| |_||_| \\_||_____|
                         Application {} Successfully started using Java ({}) {} with PID {}
                         Default language: {}. Default region: {}. Default TimeZone: {}
                         Swagger Api Url: http://{}:{}{}/doc.html""",
                 applicationName, javaVersion, javaVendor, ProcessHandle.current().pid(),
                 Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(), ZoneId.systemDefault(),
                 TechneUtil.getLocalhostStr(), serverPort, contextPath);
    }

    /**
     * 是dev环境
     *
     * @return boolean
     */
    public static boolean isDevActive() {
        return StrUtil.equals("dev", StaticConfig.active);
    }

    /**
     * 是sandbox环境
     *
     * @return boolean
     */
    public static boolean isSandboxActive() {
        return StrUtil.equals("sandbox", StaticConfig.active);
    }

    /**
     * 是prod环境
     *
     * @return boolean
     */
    public static boolean isProdActive() {
        return StrUtil.equals("prod", StaticConfig.active);
    }

}
