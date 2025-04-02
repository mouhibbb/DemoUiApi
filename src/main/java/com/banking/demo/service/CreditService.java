package com.banking.demo.service;

import com.banking.demo.entity.Compte;
import com.banking.demo.entity.Credit;
import com.banking.demo.repository.CreditRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditService {

    private final CreditRepository creditRepository;
    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }
    public List<Credit> findCreditbycompte(Compte compte){ return this.creditRepository.findCreditByCompte(compte);}
    public Credit createCredit(Credit credit) {
        return creditRepository.save(credit);
    }


    public List<Credit> getAllCredit() {
        List<Credit> credits = creditRepository.findAll();  // Récupérer tous les crédits
        for (Credit credit : credits) {
            // Assure-toi que le compte est chargé (cela devrait être fait automatiquement si la relation est bien définie)
            Compte compte = credit.getCompte();  // Obtenir le compte associé
            // Tu peux faire un traitement supplémentaire ici si nécessaire

        }
        return credits;
    }


    public Credit getCreditById(Long idCredit){
        return  creditRepository.findById(idCredit).orElse(null);
    }
    public Credit updateCredit(Long idCredit, Credit creditDetails) {
        // Récupérer l'entité existante
        Credit existingCredit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new RuntimeException("Credit not found with ID: " + idCredit));

        // Mettre à jour uniquement les champs non null de creditDetails

        if (creditDetails.getMontant() != null) {
            existingCredit.setMontant(creditDetails.getMontant());
        }
        if (creditDetails.getDureeMois() != 0) { // Utilisez 0 ou une autre valeur par défaut si dureeMois est un int
            existingCredit.setDureeMois(creditDetails.getDureeMois());
        }
        if (creditDetails.getMensualite() != null) {
            existingCredit.setMensualite(creditDetails.getMensualite());
        }
        if (creditDetails.getTypeCredit() != null) {
            existingCredit.setTypeCredit(creditDetails.getTypeCredit());
        }
        if (creditDetails.getStatusCredit() != null) {
            existingCredit.setStatusCredit(creditDetails.getStatusCredit());
        }
        if (creditDetails.getDateAccord() != null) {
            existingCredit.setDateAccord(creditDetails.getDateAccord());
        }
        if (creditDetails.getDateEcheance() != null) {
            existingCredit.setDateEcheance(creditDetails.getDateEcheance());
        }
        if (creditDetails.getCompte() != null) {
            existingCredit.setCompte(creditDetails.getCompte());
        }

        // Sauvegarder l'entité mise à jour
        return creditRepository.save(existingCredit);
    }

    @Transactional
    public void deleteCredit(Long idCredit) {
        if (creditRepository.existsById(idCredit)) {
            creditRepository.deleteById(idCredit);
        } else {
            throw new RuntimeException("Credit not found with ID: " + idCredit);
        }
    }
    public void save(Credit credit) {
        creditRepository.save(credit); // Sauvegarde le compte dans la base de données
    }

}
