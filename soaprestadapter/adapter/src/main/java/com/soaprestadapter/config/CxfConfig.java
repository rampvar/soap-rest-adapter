package com.soaprestadapter.config;

import com.soaprestadapter.interceptor.JwtTokenExtractorInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CxfConfig
 */
@Configuration
public class CxfConfig {

    /**
     * springBus bean
     * @return SpringBus bean with JwtTokenExtractorInterceptor
     * @param jwtTokenInterceptor JwtTokenExtractorInterceptor
     */
    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus(final JwtTokenExtractorInterceptor jwtTokenInterceptor) {
        SpringBus bus = new SpringBus();

        // Register your interceptor globally
        bus.getInInterceptors().add(jwtTokenInterceptor);

        // Optional: Add logging if needed
        //bus.getFeatures().add(new LoggingFeature());

        return bus;
    }
}
