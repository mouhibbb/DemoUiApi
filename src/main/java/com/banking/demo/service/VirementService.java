package com.banking.demo.service;

import com.banking.demo.entity.Virement;
import com.banking.demo.repository.VirementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VirementService {

    private final VirementRepository virementRepository;

    public VirementService(VirementRepository virementRepository ) {
        this.virementRepository = virementRepository;
    }
    // Récupérer toutes les virements
    public List<Virement> getAllVirements() {
        return virementRepository.findAll();
    }

    // Récupérer une virement par son ID
    public Virement getVirementById(Long idCompte) {
        return virementRepository.findById(idCompte).orElse(null);
    }
    // Créer une nouvelle Virement
    public Virement createVirement(Virement virement) {
        return virementRepository.save(virement);
    }

    // Mettre à jour une Virement existante
    public Virement updateVirement(Long id, Virement virementDetails) {
        Virement existingVirement = virementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Virement not found with ID: " + id));

        if (virementDetails.getMontant() != null) {
            existingVirement.setMontant(virementDetails.getMontant());
        }
        if (virementDetails.getDateVirement() != null) {
            existingVirement.setDateVirement(virementDetails.getDateVirement());
        }
        if (virementDetails.getTypeVirement() != null) {
            existingVirement.setTypeVirement(virementDetails.getTypeVirement());
        }
        if (virementDetails.getCompteSource() != null) {
            existingVirement.setCompteSource(virementDetails.getCompteSource());
        }
        if (virementDetails.getCompteDestination() != null) {
            existingVirement.setCompteDestination(virementDetails.getCompteDestination());
        }
        if (virementDetails.getCompte() != null) {
            existingVirement.setCompte(virementDetails.getCompte());
        }

        // Sauvegarder l'entité mise à jour
        return virementRepository.save(existingVirement);
    }

    // Supprimer une Virement
    public void deleteVirement(Long idVirement) {
        virementRepository.deleteById(idVirement);
        
    }

}