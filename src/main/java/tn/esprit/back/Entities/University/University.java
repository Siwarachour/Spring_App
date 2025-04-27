package tn.esprit.back.Entities.University;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tn.esprit.back.Entities.StudyPlan.StudyPlan;
import tn.esprit.back.Entities.User.User;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter

public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long universityId;

    private String name;
    private String description;
    private String programSpecialty;
    private Integer establishedYear;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String website;
    private String phoneNumber;
    private String email;
    private String type;
    private String accreditationStatus;
    private String logo;
    private Integer ranking;
    private String Thumbnail;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "university")
    private Set<StudyPlan> studyPlans;
    @ManyToMany
    private Set<User> users = new HashSet<>();
}
