package org.example.expert.global.config;

import lombok.RequiredArgsConstructor;
import org.example.expert.global.interceptor.AdminCheckInterceptor;
import org.example.expert.global.interceptor.TodoOwnerCheckInterceptor;
import org.example.expert.global.resolver.AuthUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminCheckInterceptor adminCheckInterceptor;
    private final TodoOwnerCheckInterceptor todoOwnerCheckInterceptor;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthUserArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/admin/**");
        registry.addInterceptor(todoOwnerCheckInterceptor)
                .addPathPatterns("/todos/*/managers/**");
    }
}
