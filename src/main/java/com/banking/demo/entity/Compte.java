package com.banking.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DynamicUpdate
@Table(name = "_compteBancaire")

public class Compte {
    @Override
    public String toString() {
        return "Compte{" +
                "idCompte=" + idCompte +
                ", cin=" + cin +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", date_naissance=" + date_naissance +
                ", sexe='" + sexe + '\'' +
                ", nationalité='" + nationalité + '\'' +
                ", rue='" + rue + '\'' +
                ", ville='" + ville + '\'' +
                ", code_postal=" + code_postal +
                ", pays='" + pays + '\'' +
                ", telephone=" + telephone +
                ", email='" + email + '\'' +
                ", profession='" + profession + '\'' +
                ", salaire=" + salaire +
                ", status_emploi='" + status_emploi + '\'' +
                ", type_compte='" + type_compte + '\'' +
                ", status Compte bancaire=" + statusCompteBacaire +
                ", user=" + user +
                ", credits=" + credits +
                ", virements=" + virements +
                '}';
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(Date date_naissance) {
        this.date_naissance = date_naissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getNationalité() {
        return nationalité;
    }

    public void setNationalité(String nationalité) {
        this.nationalité = nationalité;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getCode_postal() {
        return code_postal;
    }

    public void setCode_postal(int code_postal) {
        this.code_postal = code_postal;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getSalaire() {
        return salaire;
    }

    public void setSalaire(int salaire) {
        this.salaire = salaire;
    }

    public String getStatus_emploi() {
        return status_emploi;
    }

    public void setStatus_emploi(String status_emploi) {
        this.status_emploi = status_emploi;
    }

    public String getType_compte() {
        return type_compte;
    }

    public void setType_compte(String type_compte) {
        this.type_compte = type_compte;
    }


    public Long getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(Long idCompte) {
        this.idCompte = idCompte;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Set<Credit> getCredits() {
        return credits;
    }
    public void setCredits(Set<Credit> credits) {
        this.credits = credits;
    }
    public Long getCin() {
        return cin;
    }
    public Long getTelephone() {
        return telephone;
    }

    public void setTelephone(Long telephone) {
        this.telephone = telephone;
    }
    public void setCin(Long cin) {
        this.cin = cin;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompte;
    @Column(unique = true)
    private Long cin;
    private String nom;
    private String prenom;
    private Date date_naissance;
    private String sexe;
    private String nationalité;
    private String rue;
    private String ville;
    private int code_postal;
    private String pays;
    private Long telephone;
    private String email;
    private String profession;
    private int salaire;
    private String status_emploi;
    private String type_compte;

    public StatusCompteBacaire getStatusCompteBacaire() {
        return statusCompteBacaire;
    }

    public void setStatusCompteBacaire(StatusCompteBacaire statusCompteBacaire) {
        this.statusCompteBacaire = statusCompteBacaire;
    }

    private StatusCompteBacaire statusCompteBacaire;



    @JsonIgnore
    @ManyToOne User user;
    @OneToMany (cascade =  CascadeType.ALL,mappedBy = "compte",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("compte")
    private Set<Credit>credits;

    public List<Virement> getVirements() {
        return virements;
    }

    public void setVirements(List<Virement> virements) {
        this.virements = virements;
    }

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "compte")
    @JsonBackReference
    private List<Virement> virements=new ArrayList<>();


}
