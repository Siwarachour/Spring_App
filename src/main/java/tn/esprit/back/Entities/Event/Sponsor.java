package tn.esprit.back.Entities.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.back.Entities.User.User;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sponsor {@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long idSponsor;

    private String nomSponsor;
    private String descriptionSponsor;
    private String logo;
    private String contact;
    @ManyToMany(mappedBy = "sponsors", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Event> events ;

    @ManyToOne
    @JoinColumn(name = "user_id") // Clé étrangère
    @JsonIgnoreProperties("sponsors")
    private User createur;

    public User getCreateur() {
        return createur;
    }

    public void setCreateur(User createur) {
        this.createur = createur;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Long getIdSponsor() {
        return idSponsor;
    }

    public void setIdSponsor(Long idSponsor) {
        this.idSponsor = idSponsor;
    }

    public String getNomSponsor() {
        return nomSponsor;
    }

    public void setNomSponsor(String nomSponsor) {
        this.nomSponsor = nomSponsor;
    }

    public String getDescriptionSponsor() {
        return descriptionSponsor;
    }

    public void setDescriptionSponsor(String descriptionSponsor) {
        this.descriptionSponsor = descriptionSponsor;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
