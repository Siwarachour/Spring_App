package tn.esprit.back.Entities.library;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategory;
    private String name;
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private List<Document> documents = new ArrayList<>();
}

