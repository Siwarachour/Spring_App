package tn.esprit.back.Entities.Cv;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.User.User;

import java.util.List;
@EntityListeners(AuditingEntityListener.class)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "cv")
public class Cv {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String skills;

    private String experience;

    private String contactinfo;

    private String education;

@OneToOne
    User student;
    @CreatedBy
    @Column(insertable = false, updatable = false)  // Ensure this is only set on creation
    private String createdBy;


}
