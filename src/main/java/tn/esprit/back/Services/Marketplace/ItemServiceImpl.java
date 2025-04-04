package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.util.List;
import java.util.Optional;
@Service

public class ItemServiceImpl implements IItemService{
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
private UserRepository userRepository;
    @Override
    public Item ajouterItem(Item item) {
        if (item.getSeller() != null) {
            User existingSeller = userRepository.findByEmail(item.getSeller().getEmail());

            if (existingSeller != null) {
                item.setSeller(existingSeller); // Associer l'utilisateur existant
            } else {
                throw new RuntimeException("Le vendeur avec l'email " + item.getSeller().getEmail() + " n'existe pas !");
            }
        }

        return itemRepository.save(item);
    }


    @Override
    public Item updateItem(Item item) {
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
        itemRepository.deleteById(id);
    }



    @Override
    public List<Item> getItemsBySeller(Long sellerId) {
        return itemRepository.findBySellerId(sellerId);
    }
}
