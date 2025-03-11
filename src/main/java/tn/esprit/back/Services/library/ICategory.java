package tn.esprit.back.Services.library;
import tn.esprit.back.Entities.library.Category;
import java.util.List;


public interface ICategory {

    Category addCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(long idCategory );
    List<Category> getAllCategory();
    Category getCategoryById(long idCategory);


}
