package com.technehq.boot.config;

import ch.qos.logback.classic.LoggerContext;
import com.technehq.boot.component.TechneSqlDenyTurboFilter;
import com.technehq.boot.config.properties.TechneProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * SQL 日志过滤自动配置
 * <p>
 * 当 {@code techne.sql-log-filter.enabled=true} 时自动生效，
 * 将 {@link TechneSqlDenyTurboFilter} 注册到 Logback 上下文中。
 * <p>
 * application.yml 配置示例：
 * <pre>{@code
 * techne:
 *   sql-log-filter:
 *     enabled: true
 *     extra-packages:
 *       - com.example.mapper
 * }</pre>
 *
 * @author Lil' Doe
 */
@AutoConfiguration(value = "techneSqlLogFilterConfig", after = TechneProperties.class)
@ConditionalOnProperty(prefix = "techne.sql-log-filter", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class TechneSqlLogFilterConfig {

    private final TechneProperties techneProperties;

    /**
     * 将 {@link TechneSqlDenyTurboFilter} 编程式注册到 Logback 的 LoggerContext 中
     *
     * @author Lil' Doe
     *  2026/4/13 00:00
     */
    @PostConstruct
    public void registerSqlDenyTurboFilter() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        TechneSqlDenyTurboFilter filter = new TechneSqlDenyTurboFilter(
                techneProperties.getSqlLogFilter().getExtraPackages()
        );
        filter.setContext(loggerContext);
        filter.start();
        loggerContext.addTurboFilter(filter);
    }

}
