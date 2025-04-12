package tn.esprit.back.configurations;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps the '/uploads/**' URL path to the physical 'uploads' folder on the file system
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/doc/Bureau/PI/Back/src/main/resources/uploads/");

    }
}

