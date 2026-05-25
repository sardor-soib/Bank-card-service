package com.example.bankcards;

import com.example.bankcards.security.CardSecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@EnableConfigurationProperties(CardSecurityProperties.class)
public class BankRestApplication {

    private static final Logger log = LoggerFactory.getLogger(BankRestApplication.class);

    public static void main(String[] args) {
        log.info("Bank Rest application is starting...");
        var context = SpringApplication.run(BankRestApplication.class, args);

        ConfigurableEnvironment env = context.getEnvironment();
        log.info("Active profiles: {}", (Object) env.getActiveProfiles());
    }
}


