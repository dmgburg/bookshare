package com.dmgburg.bookshareserver;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/WEB-INF/",
            "classpath:/static/", "classpath:/public/" , "classpath:/" };
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/defaultCover.png")
                    .setCacheControl(CacheControl.maxAge(60, TimeUnit.DAYS))
                    .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
            registry.addResourceHandler("/**")
                    .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
        }
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(new InternalResourceViewResolver());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
