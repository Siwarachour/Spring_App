package tn.esprit.back.configurations;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${upload.dir}")
    private String uploadDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps the '/uploads/**' URL path to the physical 'uploads' folder on the file system
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/doc/Bureau/NOUVEAU/Back/Spring_App/src/main/resources/uploads","file:D:/doc/Bureau/NOUVEAU/Back/Spring_App/src/main/resources/uploads/offreimages/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir)
                .setCachePeriod(3600);
    }
}



