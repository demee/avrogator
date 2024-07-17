package org.demee.avrogator;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Ignite igniteInstance() {
        IgniteConfiguration igniteConfiguration
                = new IgniteConfiguration();
        return Ignition.start(igniteConfiguration);
    }
}
