package tn.esprit.back.Entities.Cv;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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


    @CreatedBy
    @Column(insertable = false)
    private Integer createdBy;
    @LastModifiedDate
    @Column(insertable = false)
    private Integer LastModifiedBy;


}
