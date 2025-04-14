package tn.esprit.back.configurations;



import lombok.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

import static java.nio.file.Paths.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
        private String uploadDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String absolutePath = get(uploadDir).toAbsolutePath().toString();

        // Maps the '/uploads/**' URL path to the physical 'uploads' folder on the file system
        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:D:/doc/Bureau/PI/Back/src/main/resources/uploads/")
                .addResourceLocations("file:C:/Users/friaa/OneDrive - ESPRIT/Bureau/Spring_App/src/main/resources/uploads")
                .setCachePeriod(3600);


    }
}

