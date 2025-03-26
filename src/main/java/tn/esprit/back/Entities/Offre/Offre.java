package tn.esprit.back.Entities.Offre;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.Application.Application;
import tn.esprit.back.Entities.User.User;

import java.util.Set;
@EntityListeners(AuditingEntityListener.class)
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Offre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String description;

    private String skills;

    @CreatedBy
    @Column(insertable = false)
    private Integer createdBy;

    @ManyToOne
    private User rh;

    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL)  // mappedBy points to the "offre" field in the Application entity
    private Set<Application> applications;


}
