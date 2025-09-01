package dev.xpepelok.flowly.listener;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public interface ContextRefreshedEventListener extends ApplicationListener<ContextRefreshedEvent> {
    Logger log = LoggerFactory.getLogger(ContextRefreshedEventListener.class);

    void initialize();

    @Override
    default void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {
            this.initialize();
        } catch (Exception e) {
            log.error("Unable to initialize context listener: {}", this.getClass().getName(), e);
        }
    }
}
