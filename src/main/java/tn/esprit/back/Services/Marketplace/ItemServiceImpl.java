package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Repository.Marketplace.ItemRepository;

import java.util.List;
import java.util.Optional;
@Service

public class ItemServiceImpl implements IItemService{
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item ajouterItem(Item item) {
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
