package tn.esprit.back.Entities.Offre;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.Application.Application;
import tn.esprit.back.Entities.User.User;

import java.util.HashSet;
import java.util.Set;

@EntityListeners(AuditingEntityListener.class)
@Builder
@Entity
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

    private String imageUrl;  // 💡 chemin ou lien vers l'image uploadée

    @CreatedBy
    @Column(insertable = false)
    private Integer createdBy;

    @JsonIgnore
    @ManyToOne
    private User rh;

    public void setRh(User rh) {
        this.rh = rh;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL)
    private Set<Application> applications;

    // 🧠 Initialisation pour éviter les NullPointer
    public Offre() {
        this.applications = new HashSet<>();
    }

}
