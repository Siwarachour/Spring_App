package tn.esprit.back.Entities.library;
import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.back.Entities.User.User;
import java.time.LocalDateTime;



@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReview;
    private String comments;
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
    private LocalDateTime reviewDate;



    @ManyToOne
    private User reviewer;
    @OneToOne
    private Document document;
}