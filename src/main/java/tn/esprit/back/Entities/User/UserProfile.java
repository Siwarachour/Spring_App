package tn.esprit.back.Entities.User;

import tn.esprit.back.Entities.Projet.Projet;

import java.util.List;

public class UserProfile {


    private String email;
    private String FirstName;
    private String LastName;
    private String imageUrl;
    private List<Projet> projetsCrees;
private String address;
    private String phone;

    public UserProfile( String email, String FirstName, String LastName , String imageUrl,String Address, String Phone,List<Projet> projetsCrees) {
        this.email = email;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.imageUrl = imageUrl;
        this.address = Address;
        this.phone = Phone;

        this.projetsCrees = projetsCrees;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Projet> getProjetsCrees() {
        return projetsCrees;
    }

    public void setProjetsCrees(List<Projet> projetsCrees) {
        this.projetsCrees = projetsCrees;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }
}
