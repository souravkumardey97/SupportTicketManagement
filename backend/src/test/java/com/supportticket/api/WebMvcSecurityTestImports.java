package com.supportticket.api;

import com.supportticket.config.RestAccessDeniedHandler;
import com.supportticket.config.RestAuthenticationEntryPoint;
import com.supportticket.config.SecurityConfig;
import com.supportticket.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        SecurityConfig.class,
        RestAccessDeniedHandler.class,
        RestAuthenticationEntryPoint.class,
        GlobalExceptionHandler.class,
        SecurityWebMvcTestConfiguration.class
})
public @interface WebMvcSecurityTestImports {
}
