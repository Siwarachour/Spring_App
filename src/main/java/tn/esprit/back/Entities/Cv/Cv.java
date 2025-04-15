package tn.esprit.back.Entities.Cv;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.User.User;

import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "cv_experience", joinColumns = @JoinColumn(name = "cv_id"))
    @Column(name = "experience_entry", columnDefinition = "TEXT")
    private List<String> experiences;


    @ElementCollection
    @CollectionTable(name = "cv_education", joinColumns = @JoinColumn(name = "cv_id"))
    @Column(name = "education_entry")
    private List<String> educations;

    @ElementCollection
    @CollectionTable(name = "cv_projects", joinColumns = @JoinColumn(name = "cv_id"))
    @Column(name = "project_entry")
    private List<String> projects;

    @ElementCollection
    @CollectionTable(name = "cv_languages", joinColumns = @JoinColumn(name = "cv_id"))
    @Column(name = "language_entry")
    private List<String> languages; // Store languages as a list of strings

    @ElementCollection
    @CollectionTable(name = "cv_hobbies", joinColumns = @JoinColumn(name = "cv_id"))
    @Column(name = "hobby_entry")
    private List<String> hobbies;

    @Column(nullable = true)
    private String contactinfo;

    @Column(nullable = true)
    private String pdfDownloadLink;

    @Column(nullable = true)
    private String photoUrl = "C:/Users/21650/Desktop/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/uploads/photo.jpg"; // Default value for photoUrl

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String linkedinProfile;

    @Column(nullable = true)
    private String certificate;

    @OneToOne
    @JsonIgnore
    private User student;

    @CreatedBy
    @Column(insertable = false, updatable = false)
    private String createdBy;

    @JsonGetter("languages")
    public List<String> getLanguages() {
        return languages;
    }

    @Override
    public String toString() {
        return "Cv{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", skills='" + skills + '\'' +
                ", experiences=" + experiences +
                ", educations=" + educations +
                ", projects=" + projects +
                ", languages=" + languages +
                ", hobbies=" + hobbies +
                ", contactinfo='" + contactinfo + '\'' +
                ", pdfDownloadLink='" + pdfDownloadLink + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", linkedinProfile='" + linkedinProfile + '\'' +
                ", certificate='" + certificate + '\'' +
                ", student=" + student +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
