package co.pshekhar.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ExecutorConfig {
    @Bean(name = "virtualThreadExecutor")
    ExecutorService createVirtualThreadExecutor() {
        ThreadFactory factory = Thread.ofVirtual().name("virtual-thread-", 0L).factory();
        return Executors.newThreadPerTaskExecutor(factory);
    }

    @Bean(name = "virtualThreadScheduler")
    Scheduler createVirtualThreadBackedScheduler() {
        return Schedulers.fromExecutor(createVirtualThreadExecutor());
    }
}
