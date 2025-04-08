package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Entities.Role.RoleName;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.PanierRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
public class PanierServiceImpl implements PanierService {
    @Autowired
    private PanierRepository panierRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Panier ajouterPanier(Panier panier) {
        return panierRepository.save(panier);

    }

    @Override
    public Panier updatePanier(Panier panier) {
        return panierRepository.save(panier);
    }

    @Override
    public List<Panier> getAllPaniers() {
        return panierRepository.findAll();
    }

    @Override
    public Optional<Panier> getPanierById(Long id) {
        return panierRepository.findById(id);
    }

    @Override
    public void supprimerPanier(Long id) {
        panierRepository.deleteById(id);
    }

    @Override
    public Panier ajouterItemAuPanier(Long panierId, Long itemId) {
        // Récupérer l'email de l'utilisateur connecté
        String userEmail = getCurrentUserEmail();

        // Récupérer l'utilisateur par son email
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable avec l'email : " + userEmail);
        }

        // Vérifier que l'utilisateur a bien le rôle 'ROLE_CLIENT' et l'associer comme acheteur
        if (user.getRole().getName() == RoleName.ROLE_CLIENT) {
            // Récupérer le panier et l'article
            Panier panier = panierRepository.findById(panierId).orElseThrow();
            Item item = itemRepository.findById(itemId).orElseThrow();

            // Ajouter l'article au panier
            panier.getItems().add(item);

            // Associer le panier à l'utilisateur (acheteur)
            panier.setBuyer(user); // L'utilisateur devient l'acheteur de ce panier

            // Sauvegarder le panier
            return panierRepository.save(panier);
        } else {
            throw new RuntimeException("Seul un client peut ajouter un article à son panier !");
        }
    }

    @Override
    public Panier supprimerItemDuPanier(Long panierId, Long itemId) {
        // Récupérer l'email de l'utilisateur connecté
        String userEmail = getCurrentUserEmail();

        // Récupérer l'utilisateur par son email
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable avec l'email : " + userEmail);
        }

        // Vérifier que l'utilisateur a bien le rôle 'ROLE_CLIENT'
        if (user.getRole().getName() == RoleName.ROLE_CLIENT) {
            // Récupérer le panier et l'article
            Panier panier = panierRepository.findById(panierId).orElseThrow();
            Item item = itemRepository.findById(itemId).orElseThrow();

            // Supprimer l'article du panier
            panier.getItems().remove(item);

            // Sauvegarder les modifications du panier
            return panierRepository.save(panier);
        } else {
            throw new RuntimeException("Seul un client peut supprimer un article de son panier !");
        }
    }


    @Override
    public BigDecimal calculerTotalPanier(Long panierId) {
        Panier panier = panierRepository.findById(panierId).orElseThrow();
        return panier.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Somme avec BigDecimal
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }

}