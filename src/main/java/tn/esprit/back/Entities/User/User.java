package tn.esprit.back.Entities.User;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.Role.Role;
import tn.esprit.back.Entities.library.Department;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Entities.library.Review;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String phone;
    private String address;
    @Temporal(TemporalType.DATE)
    private Date birthday;
    private boolean enabled;
    private boolean accountLocked;
    //private String role;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Role> roles;


/*
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastmodifiedDate;*/



    @ManyToOne
    private Department department;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Document> documents = new ArrayList<>();
    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviews = new ArrayList<>();

}
