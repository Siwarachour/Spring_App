package models;

import jakarta.persistence.*;


@Entity
@Table(name = "cv")
public class Cv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cvId;   // Primary Key

    private String name;
    private String skills;
    private String experience;
    private String education;
    private String contactInfo;

    // Default constructor
    public Cv() {
    }

    // Constructor with parameters
    public Cv(String name, String skills, String experience, String education, String contactInfo) {
        this.name = name;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
        this.contactInfo = contactInfo;
    }

    // Getters and Setters
    public Long getCvId() {
        return cvId;
    }

    public void setCvId(Long cvId) {
        this.cvId = cvId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
