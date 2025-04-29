package tn.esprit.back.Services.Marketplace;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.Marketplace.FactureRepository;
import tn.esprit.back.Repository.Marketplace.PaiementRepository;
import tn.esprit.back.Repository.Marketplace.PanierRepository;
import tn.esprit.back.Repository.User.UserRepository;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PanierRepository panierRepository;
    private final FactureRepository factureRepository;
    private final PaiementRepository paiementRepository;
    private final UserRepository userRepository;

    @Value("${stripe.api.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public Map<String, String> initierPaiement(int userId) throws StripeException {
        final double MINIMUM_AMOUNT = 0.50;

        Panier panier = panierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (panier.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = panier.getItems().stream()
                .mapToDouble(Item::getPrice)
                .sum();

        if (total < MINIMUM_AMOUNT) {
            throw new RuntimeException("Minimum payment amount is " + MINIMUM_AMOUNT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (total * 100))
                .setCurrency("eur")
                .setCustomer(user.getStripeCustomerId())
                .putMetadata("user_id", String.valueOf(userId))
                .putMetadata("cart_id", String.valueOf(panier.getIdPanier()))
                .setReceiptEmail(user.getEmail())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return Map.of(
                "clientSecret", paymentIntent.getClientSecret(),
                "paymentIntentId", paymentIntent.getId()
        );
    }

    @Transactional
    public Facture confirmerPaiementReussi(String paymentIntentId) throws StripeException {
        log.info("Confirming payment intent: {}", paymentIntentId);

        try {
            // Retrieve payment intent with expanded charge
            PaymentIntent paymentIntent = PaymentIntent.retrieve(
                    paymentIntentId,
                    Map.of("expand", List.of("latest_charge")),
                    RequestOptions.getDefault()
            );

            log.info("PaymentIntent status: {}", paymentIntent.getStatus());

            if (!"succeeded".equals(paymentIntent.getStatus())) {
                throw new RuntimeException("Payment not succeeded. Current status: " + paymentIntent.getStatus());
            }

            // Validate metadata
            if (paymentIntent.getMetadata() == null ||
                    !paymentIntent.getMetadata().containsKey("user_id") ||
                    !paymentIntent.getMetadata().containsKey("cart_id")) {
                throw new RuntimeException("Invalid payment intent metadata");
            }

            int userId = Integer.parseInt(paymentIntent.getMetadata().get("user_id"));
            String cartId = paymentIntent.getMetadata().get("cart_id");

            // Check if already processed
            if (factureRepository.existsByStripePaymentIntentId(paymentIntentId)) {
                log.warn("Payment already processed: {}", paymentIntentId);
                return factureRepository.findByStripePaymentIntentId(paymentIntentId)
                        .orElseThrow(() -> new RuntimeException("Existing invoice not found"));
            }

            // Get user and cart
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found for id: " + userId));

            Panier panier = panierRepository.findById(Long.valueOf(cartId))
                    .orElseThrow(() -> new RuntimeException("Cart not found for id: " + cartId));

            // Create invoice
            Facture facture = createInvoice(paymentIntent, user, panier);
            createPaymentRecord(paymentIntent, facture);
            clearCart(panier);

            return facture;
        } catch (Exception e) {
            log.error("Failed to confirm payment", e);
            throw e;
        }
    }

    private Facture createInvoice(PaymentIntent paymentIntent, User user, Panier panier) {
        Facture facture = Facture.builder()
                .client(user)
                .items(new HashSet<>(panier.getItems()))
                .montantTotal(paymentIntent.getAmount() / 100.0)
                .dateCreation(new Date())
                .status(Facture.PaymentStatus.SUCCEEDED)
                .stripePaymentIntentId(paymentIntent.getId())
                .paymentMethod(PaymentMethod.CARTE_BANCAIRE)
                .paymentReceiptUrl(paymentIntent.getLatestChargeObject().getReceiptUrl())
                .datePaiement(new Date())
                .build();

        return factureRepository.save(facture);
    }

    private void createPaymentRecord(PaymentIntent paymentIntent, Facture facture) {
        Paiement paiement = Paiement.builder()
                .facture(facture)
                .stripePaymentIntentId(paymentIntent.getId())
                .stripeChargeId(paymentIntent.getLatestCharge())
                .montant(paymentIntent.getAmount() / 100.0)
                .datePaiement(new Date())
                .methode(PaymentMethod.CARTE_BANCAIRE)
                .status(Paiement.PaymentStatus.SUCCEEDED)
                .receiptUrl(paymentIntent.getLatestChargeObject().getReceiptUrl())
                .build();

        paiementRepository.save(paiement);
    }

    @Transactional(readOnly = true)
    public List<Paiement> getPaiementsByUserId(int userId) {
        return paiementRepository.findAllByFactureClientId(userId);
    }

    @Transactional(readOnly = true)
    public List<Facture> getFacturesByUserId(int userId) {
        return factureRepository.findAllByClientId(userId);
    }

    @Transactional(readOnly = true)
    public List<Facture> getAllFactures() {
        return factureRepository.findAllByOrderByDateCreationDesc();
    }
    private void clearCart(Panier panier) {
        panier.getItems().clear();
        panier.setTotal(0.0);
        panierRepository.save(panier);
    }
    @Transactional
    public void deleteFacture(Long factureId) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture not found"));

        // Supprimez d'abord le paiement associé s'il existe
        paiementRepository.findByFacture_IdFacture(factureId).ifPresent(paiementRepository::delete);

        // Puis supprimez la facture
        factureRepository.delete(facture);
    }
}