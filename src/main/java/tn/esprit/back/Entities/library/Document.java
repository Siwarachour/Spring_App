package tn.esprit.back.Entities.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long idDocument;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String fileUrl;
    private LocalDateTime uploadDate;
    private String keywords;
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;


    @JsonIgnore
    @ManyToOne
    private User student;

    @JsonIgnore
    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private Review review;

    @JsonIgnore
    @ManyToMany
    private List<Category> categories = new ArrayList<>();

    public Document() {
        this.uploadDate = LocalDateTime.now();
    }

    // Constructor for file upload
    public Document(String title, String description, String documentType,
                    String fileUrl, String keywords, String status, String uploadDate) {
        this();
        this.title = title;
        this.description = description;
        this.documentType = DocumentType.valueOf(documentType);
        this.fileUrl = fileUrl;
        this.keywords = keywords;
        this.status = DocumentStatus.valueOf(status);
    }
}


