package tn.esprit.back.Entities.Application;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.Cv.Cv;
import tn.esprit.back.Entities.Feedback.Feedback;
import tn.esprit.back.Entities.Offre.Offre;
import tn.esprit.back.Entities.User.User;

import java.util.List;
@EntityListeners(AuditingEntityListener.class)
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Application {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


 @ManyToOne
 User student;



     @ManyToOne
     Offre offre;

     @Enumerated(EnumType.STRING)
     ApplicationStatus status;

@OneToOne(cascade = CascadeType.ALL)
     Feedback feedback;

     @OneToOne
     Cv cv;


}
