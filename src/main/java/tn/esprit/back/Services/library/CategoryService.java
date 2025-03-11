package tn.esprit.back.Services.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.library.Category;
import tn.esprit.back.Repository.library.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategory {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(long idCategory) {
        categoryRepository.deleteById(idCategory);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(long idCategory) {
        Optional<Category> category = categoryRepository.findById(idCategory);
        return category.orElse(null);
    }
}
