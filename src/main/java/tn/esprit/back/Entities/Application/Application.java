package tn.esprit.back.Entities.Application;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.Cv.Cv;
import tn.esprit.back.Entities.Feedback.Feedback;
import tn.esprit.back.Entities.Offre.Offre;
import tn.esprit.back.Entities.User.User;


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

   //gfd
   @ManyToOne
   @JsonIgnore
   private User student;

    @Column(nullable = true)
    private String condidat;

    private String motivatedlettre;

     @ManyToOne
     Offre offre;




     @Enumerated(EnumType.STRING)
     ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    InterviewStatus result;


    @OneToOne( optional = true)
    @ManyToOne(optional = true)
    private Feedback feedback;

    @JsonGetter("studentId")  // This will be the name of the field in the JSON response
    public Integer getStudentId() {
        return student != null ? student.getId() : null;
    }
    @JsonGetter("username")  // This will be the name of the field in the JSON response
    public String getStudentname() {
        return student != null ? student.getUsername() : null;
    }

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id", nullable = false)
    private Cv cv;


    @JsonGetter("offreId")
    public Integer getOffreId() {
        return offre != null ? offre.getId() : null;
    }
    @JsonGetter("cvid")
    public Integer getCvid() {
        return cv != null ? cv.getId() : null;
    }
    @JsonGetter("skills")
    public String getSkills() {
        return cv != null ? cv.getSkills() : null;
    }

    @JsonGetter("pdfDownloadLink")
    public String getCvLinkPdf() {
        return  cv.getPdfDownloadLink() ;
    }

    @CreatedBy
    @Column(insertable = false)
    private Integer createdBy;
}
