package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.Role.RoleName;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Item ajouterItem(Item item) {
        // Récupérer l'email de l'utilisateur connecté
        String userEmail = getCurrentUserEmail();

        // Récupérer l'utilisateur par son email
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable avec l'email : " + userEmail);
        }

        // Vérifier que l'utilisateur a bien le rôle 'ROLE_CLIENT' et l'associer comme vendeur
        if (user.getRole().getName() == RoleName.ROLE_CLIENT) {
            item.setSeller(user); // L'utilisateur devient le vendeur de cet article
        } else {
            throw new RuntimeException("L'utilisateur doit être un client pour ajouter un article !");
        }

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        // Récupérer l'email de l'utilisateur connecté
        String userEmail = getCurrentUserEmail();

        // Récupérer l'utilisateur par son email
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable avec l'email : " + userEmail);
        }

        // Vérifier que l'utilisateur est bien le vendeur de l'article qu'il veut modifier
        if (item.getSeller().getId() != user.getId()) {
            throw new RuntimeException("Vous ne pouvez pas modifier un article qui ne vous appartient pas.");
        }

        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public void supprimerItem(Long id) {
        // Récupérer l'email de l'utilisateur connecté
        String userEmail = getCurrentUserEmail();

        // Récupérer l'utilisateur par son email
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable avec l'email : " + userEmail);
        }

        // Vérifier que l'utilisateur est bien le vendeur de l'article qu'il veut supprimer
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            if (item.getSeller().getId() != user.getId()) {
                throw new RuntimeException("Vous ne pouvez pas supprimer un article qui ne vous appartient pas.");
            }

            itemRepository.delete(item);
        } else {
            throw new RuntimeException("Article non trouvé avec l'ID : " + id);
        }
    }

    @Override
    public List<Item> getItemsBySeller(int sellerId) {
        return itemRepository.findBySellerId(sellerId);
    }

    // Méthode pour récupérer l'email de l'utilisateur connecté
    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();  // retourne l'email de l'utilisateur connecté
        } else {
            return principal.toString();  // Si ce n'est pas un UserDetails, retourne le principal sous forme de chaîne
        }
    }
}
