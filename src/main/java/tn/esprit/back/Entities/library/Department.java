package tn.esprit.back.Entities.library;

import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.back.Entities.User.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;




    @OneToMany(mappedBy = "department")
    private List<User> users = new ArrayList<>();
}