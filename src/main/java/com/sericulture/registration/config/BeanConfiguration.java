package com.sericulture.registration.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BeanConfiguration {


    @Bean
    public ModelMapper modelMapper() {
        // return new ModelMapper();
        ModelMapper MAPPER = new ModelMapper();
        MAPPER.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return MAPPER;
    }
    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    @Bean
    @Profile("!prod")
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
