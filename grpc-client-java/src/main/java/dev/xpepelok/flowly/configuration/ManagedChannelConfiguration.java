package dev.xpepelok.flowly.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.internal.DnsNameResolverProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManagedChannelConfiguration {
    @Bean
    public ManagedChannel managedChannel(
            @Value("${app.service.host:localhost}") String serviceHost,
            @Value("${app.service.port:6979}") int servicePort
    ) {
        NameResolverRegistry.getDefaultRegistry().register(new DnsNameResolverProvider());
        return ManagedChannelBuilder.forTarget(String.format("%s:%d", serviceHost, servicePort))
                .usePlaintext()
                .build();
    }
}
