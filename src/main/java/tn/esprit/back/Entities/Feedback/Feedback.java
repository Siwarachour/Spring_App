package tn.esprit.back.Entities.Feedback;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Feedback")
public class Feedback {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double note;

    private String commentaire;

    @CreatedBy
    @Column(insertable = false)
    private Integer createdBy;

}
