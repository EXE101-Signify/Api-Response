package fptu.exe202.signify.apiresponse.config;

import fptu.exe202.signify.apiresponse.handler.GlobalExceptionHandler;
import fptu.exe202.signify.apiresponse.handler.SecurityExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot auto-configuration for the api-response library.
 * <p>
 * Registers {@link GlobalExceptionHandler} automatically when a servlet-based
 * web application is detected. Spring Security exception handling is conditionally
 * enabled only when Spring Security is on the classpath.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ApiResponseAutoConfiguration {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * Conditional configuration that registers {@link SecurityExceptionHandler}
     * only when Spring Security is present on the classpath.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.security.core.AuthenticationException")
    static class SecurityExceptionHandlerConfiguration {

        @Bean
        public SecurityExceptionHandler securityExceptionHandler() {
            return new SecurityExceptionHandler();
        }
    }
}
