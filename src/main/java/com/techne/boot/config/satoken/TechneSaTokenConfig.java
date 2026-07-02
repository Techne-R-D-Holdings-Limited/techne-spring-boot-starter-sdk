package com.techne.boot.config.satoken;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.techne.boot.constants.TechneConstants;
import com.techne.boot.jackson.*;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * TechneSaTokenConfigurer
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface TechneSaTokenConfig extends WebMvcConfigurer {

    /**
     * 基于路由的拦截式鉴权，实现此方法时需要手动进行鉴权，例如调用：StpUtil.checkLogin()
     *
     * @return TechneInterceptor
     */
    TechneInterceptor saRouteBuild();

    /**
     * 注册Sa-Token的注解拦截器，打开注解式鉴权功能
     *
     * @param registry registry
     */
    @Override
    default void addInterceptors(InterceptorRegistry registry) {
        // 注册注解拦截器
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(this.saRouteBuild());
        interceptorRegistration.addPathPatterns("/**").excludePathPatterns(TechneConstants.EXCLUDE_URL);
        String[] excludeUrl = SpringUtil.getProperty("techne.exclude-url", String[].class, null);
        if (ArrayUtil.isNotEmpty(excludeUrl)) {
            interceptorRegistration.excludePathPatterns(excludeUrl);
        }
    }

    /**
     * 除了默认注册的转换器和格式化程序之外，还添加Converters和Formatters程序。
     *
     * @param registry registry
     */
    @Override
    default void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldAnnotation(new PhoneNumberAnnotationFormatterFactory());
        registry.addFormatterForFieldAnnotation(new NumZeroFormatAnnotationFormatterFactory());
        registry.addFormatterForFieldAnnotation(new BigDecimalFormatAnnotationFormatterFactory());
        registry.addFormatterForFieldAnnotation(new CurrencyConversionAnnotationFormatterFactory());
        registry.addFormatterForFieldAnnotation(new SortColumnAnnotationFormatterFactory());
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }

}
