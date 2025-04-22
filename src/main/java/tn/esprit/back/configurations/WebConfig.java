package tn.esprit.back.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/offreimages/**")
                .addResourceLocations("file:D:/doc/Bureau/NOUVEAU/Back/Spring_App/uploads/offreimages/");
    }
}
