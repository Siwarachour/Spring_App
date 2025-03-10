package models;

import jakarta.persistence.*;


import java.util.Date;

@Entity
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;   // Primary Key

    private String offerDetails;       // Detailed description of the offer

    private String validityPeriod;     // Period the offer is valid (e.g., "2025-01-01 to 2025-03-31")

    private String skillsRequired;     // Required skills (e.g., "Java, Spring Boot, SQL")

    private String companyName;        // Name of the company offering the position

    private String location;           // Location of the job or service

    private String salaryRange;        // Salary range for the position (e.g., "50,000 - 70,000 USD")

    private Date applicationDeadline;  // Application deadline for the offer

    // Default constructor
    public Offre() {
    }

    // Constructor with all fields
    public Offre(String offerDetails, String validityPeriod, String skillsRequired,
                 String companyName, String location, String salaryRange, Date applicationDeadline) {
        this.offerDetails = offerDetails;
        this.validityPeriod = validityPeriod;
        this.skillsRequired = skillsRequired;
        this.companyName = companyName;
        this.location = location;
        this.salaryRange = salaryRange;
        this.applicationDeadline = applicationDeadline;
    }

    // Getters and Setters
    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public Date getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(Date applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    @Override
    public String toString() {
        return "Offre{" +
                "offerId=" + offerId +
                ", offerDetails='" + offerDetails + '\'' +
                ", validityPeriod='" + validityPeriod + '\'' +
                ", skillsRequired='" + skillsRequired + '\'' +
                ", companyName='" + companyName + '\'' +
                ", location='" + location + '\'' +
                ", salaryRange='" + salaryRange + '\'' +
                ", applicationDeadline=" + applicationDeadline +
                '}';
    }
}
