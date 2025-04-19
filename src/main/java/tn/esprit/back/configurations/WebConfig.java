package tn.esprit.back.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map '/uploads/**' to the physical directory under 'src/main/resources/uploads'
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/uploads/");  // Maps to 'src/main/resources/uploads/'
    }
}
