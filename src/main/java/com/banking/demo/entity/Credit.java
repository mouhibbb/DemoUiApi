package com.banking.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_credit")
public class Credit {
    public Long getIdCredit() {return idCredit;}

    public void setIdCredit(Long idCredit) {
        this.idCredit = idCredit;
    }





    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public int getDureeMois() {
        return dureeMois;
    }

    public void setDureeMois(int dureeMois) {
        this.dureeMois = dureeMois;
    }

    public Double getMensualite() {
        return mensualite;
    }

    public void setMensualite(Double mensualite) {
        this.mensualite = mensualite;
    }




    public String getDateAccord() {
        return dateAccord;
    }

    public void setDateAccord(String dateAccord) {
        this.dateAccord = dateAccord;
    }

    public String getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(String dateEcheance) {
        this.dateEcheance = dateEcheance;
    }
    public CreditType getTypeCredit() {
        return typeCredit;
    }
    public void setTypeCredit(CreditType typeCredit) {
        this.typeCredit = typeCredit;
    }
    public Compte getCompte() {
        return compte;
    }
    public void setCompte(Compte compte) {
        this.compte = compte;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long idCredit;
    @Column
    private Double montant;
    @Column
    private int dureeMois;
    @Column
    private Double mensualite;
    private CreditType typeCredit;


    public StatusCredit getStatusCredit() {
        return statusCredit;
    }

    public void setStatusCredit(StatusCredit statusCredit) {
        this.statusCredit = statusCredit;
    }

    private StatusCredit statusCredit;
    private String dateAccord;
    private String dateEcheance;
    @JsonIgnoreProperties("credits")
    @ManyToOne Compte compte;


}
