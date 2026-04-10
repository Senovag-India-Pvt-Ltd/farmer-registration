package com.sericulture.registration.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BeanConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper MAPPER = new ModelMapper();
        MAPPER.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return MAPPER;
    }

    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    /**
     * FIX: Shared RestTemplate bean with connection pooling.
     * Previously, FarmerService.getCaste() and getVillageDetails() created
     * a new RestTemplate on every single request — no connection reuse,
     * causing connection exhaustion under load.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebMvcConfigurer corsMappingConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .maxAge(3000)
                        .allowedHeaders("*")
                        .exposedHeaders("*");
            }
        };
    }
}
