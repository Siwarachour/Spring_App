package tn.esprit.back.configurations;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.secret-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        if (stripeApiKey == null || stripeApiKey.isEmpty()) {
            throw new IllegalStateException("Stripe API key is not configured");
        }
        Stripe.apiKey = stripeApiKey;
    }
}