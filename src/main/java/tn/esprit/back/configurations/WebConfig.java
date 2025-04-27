package tn.esprit.back.configurations;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuration pour les images des offres
        registry.addResourceHandler("/offreimages/**")
                .addResourceLocations("file:D:/doc/Bureau/NOUVEAU/Back/Spring_App/uploads/offreimages/");

        // Nouvelle configuration pour les images des items
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:C:/Users/friaa/OneDrive - ESPRIT/Bureau/Spring_App/src/main/resources/uploads/")
                .setCachePeriod(3600);

        // Optionnel: Configuration pour les ressources statiques générales
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
