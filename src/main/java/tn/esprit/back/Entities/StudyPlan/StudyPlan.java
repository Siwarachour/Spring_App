package tn.esprit.back.Entities.StudyPlan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tn.esprit.back.Entities.University.University;

import java.util.Date;

@Entity
@Getter
@Setter

public class StudyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyPlanId;
    private String programName;
    private String programCode;
    private Integer durationYears;
    private String degreeType;
    private Integer totalCredits;
    private Integer coreCourses;
    private String languageOfInstruction;
    private String specialization;
    private String programDescription;
    private String prerequisites;
    private String admissionRequirements;
    @ManyToOne
    private University university;
    private Date startDate;
    private Date endDate;
    private String programWebsite;

    public Long getUniversity() {
        return university.getUniversityId();
    }
}
