package tn.esprit.back.Entities.library;

import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.back.Entities.User.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String fileUrl;
    private LocalDateTime uploadDate;
    private String keywords;
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;


    @ManyToOne
    private User student;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private Review review;

    @ManyToMany
    private List<Category> categories = new ArrayList<>();
}