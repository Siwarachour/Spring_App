package tn.esprit.back.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL '/uploads/**' to your file system directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/C:/Users/21650/Desktop/Spring_App/uploads/");
    }
}
