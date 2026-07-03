package com.technehq.boot.component;

import com.technehq.boot.util.TechneThreadUtil;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.context.LifecycleProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 确保应用退出时关闭一些东西
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShutdownManager {

    private final ScheduledExecutorService scheduledExecutorService;

    private final LifecycleProperties lifecycleProperties;

    /**
     * destroy
     */
    @PreDestroy
    public void destroy() {
        TechneThreadUtil.shutdownAndAwaitTermination(scheduledExecutorService, lifecycleProperties.getTimeoutPerShutdownPhase());
    }

}