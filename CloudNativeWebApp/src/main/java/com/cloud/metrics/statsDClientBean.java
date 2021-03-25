package com.cloud.metrics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class statsDClientBean {
    @Bean
    StatsDClient getStatsDClient(){
        return new NonBlockingStatsDClient("csye6225", "", 8125);
    }
}
