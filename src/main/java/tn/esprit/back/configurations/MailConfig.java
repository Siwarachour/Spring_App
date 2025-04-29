package tn.esprit.back.configurations;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import tn.esprit.back.Entities.User.User;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);  // Port pour TLS
        mailSender.setUsername("siwarachour999@gmail.com");  // Votre adresse Gmail
        mailSender.setPassword("bwng ccgy wooq epvu");  // Utilisez le mot de passe d'application généré

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");  // Activer TLS
        props.put("mail.smtp.auth", "true");  // Authentification requise
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return mailSender;
    }

    public void sendAccountCreationEmail(User user, JavaMailSender javaMailSender) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Bienvenue sur notre plateforme WorkMate !");
        message.setText("Bonjour " + user.getFirstName() + ",\n\nVotre compte a été créé avec succès.\n\nIdentifiant : " + user.getUsername());

        javaMailSender.send(message);
    }



}
