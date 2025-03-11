package tn.esprit.back.Controllers.library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.library.Category;
import tn.esprit.back.Services.library.ICategory;
import java.util.List;


@RestController
@RequestMapping("/category")  // Base URL for Category
public class CategoryController {

    @Autowired
    ICategory categoryService;

    @PutMapping("/update")
    public Category updateCategory(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    @PostMapping("/add")
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @GetMapping("/getall")
    public List<Category> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @GetMapping("/retrieve/{idCategory}")
    public Category getCategoryById(@PathVariable long idCategory) {
        return categoryService.getCategoryById(idCategory);
    }

    @DeleteMapping("/delete/{idCategory}")
    public void deleteCategory(@PathVariable long idCategory) {
        categoryService.deleteCategory(idCategory);
    }
}
