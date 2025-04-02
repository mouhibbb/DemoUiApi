package com.banking.demo.DTO;

import java.util.List;

public class TransactionRequestDTO {
    private Long idCompteSource;

    private Long idCompteCible;

    private Long montant;

    public Long getIdCompteSource() {
        return idCompteSource;
    }

    public void setIdCompteSource(Long idCompteSource) {
        this.idCompteSource = idCompteSource;
    }

    public Long getIdCompteCible() {
        return idCompteCible;
    }

    public void setIdCompteCible(Long idCompteCible) {
        this.idCompteCible = idCompteCible;
    }

    public Long getMontant() {
        return montant;
    }

    public void setMontant(Long montant) {
        this.montant = montant;
    }
}
