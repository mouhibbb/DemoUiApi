package com.banking.demo.service;

import com.banking.demo.entity.StatusCompteBacaire;
import com.banking.demo.entity.User;
import com.banking.demo.repository.CompteRepository;
import org.springframework.stereotype.Service;
import com.banking.demo.entity.Compte;

import java.util.List;
import java.util.Optional;

@Service
public class CompteService {

    private final CompteRepository compteRepository;
    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }
    public List<Compte> getAllComptes(){
        return compteRepository.findAll();
    }
    public Compte getCompteById(Long idCompte) {
        return compteRepository.findById(idCompte).orElse(null);
    }

    public Compte createCompte(Compte   compte) {
        return compteRepository.save(compte);
    }
    public List<Compte> getCompteEnCours(){return compteRepository.findByStatusCompteBacaire(StatusCompteBacaire.En_cours);}

    public Compte updateCompte(Long idCompte, Compte compteDetails) {
        Compte compte = compteRepository.findById(idCompte)
                .orElseThrow(() -> new RuntimeException("Compte not found"));
        if (compteDetails.getNom() != null) {
            compte.setNom(compteDetails.getNom());
        }
        if (compteDetails.getPrenom() != null) {
            compte.setPrenom(compteDetails.getPrenom());
        }
        if (compteDetails.getDate_naissance() != null) {
            compte.setDate_naissance(compteDetails.getDate_naissance());
        }
        if (compteDetails.getSexe() != null) {
            compte.setSexe(compteDetails.getSexe());
        }
        if (compteDetails.getNationalité() != null) {
            compte.setNationalité(compteDetails.getNationalité());
        }
        if (compteDetails.getRue() != null) {
            compte.setRue(compteDetails.getRue());
        }
        if (compteDetails.getVille()!= null) {
            compte.setVille(compteDetails.getVille());
        }
        if (compteDetails.getCode_postal() != 0) {
            compte.setCode_postal(compteDetails.getCode_postal());
        }
        if (compteDetails.getPays()!= null) {
            compte.setPays(compteDetails.getPays());
        }
        if (compteDetails.getTelephone() != null) {
            compte.setTelephone(compteDetails.getTelephone());
        }
        if (compteDetails.getEmail() != null) {
            compte.setEmail(compteDetails.getEmail());
        }
        if (compteDetails.getProfession() != null) {
            compte.setProfession(compteDetails.getProfession());
        }
        if (compteDetails.getSalaire() != 0) {
            compte.setSalaire(compteDetails.getSalaire());
        }
        if (compteDetails.getStatus_emploi() != null) {
            compte.setStatus_emploi(compteDetails.getStatus_emploi());
        }
        if (compteDetails.getType_compte() != null) {
            compte.setType_compte(compteDetails.getType_compte());
        }
        if (compteDetails.getCin() != null) {
            compte.setCin(compteDetails.getCin());
        }

        if (compteDetails.getUser() != null) {
            compte.setUser(compteDetails.getUser());
        }
        if (compteDetails.getCredits() != null) {
            compte.setCredits(compteDetails.getCredits());
        }
        if (compteDetails.getVirements() != null) {
            compte.setVirements(compteDetails.getVirements());
        }


        return compteRepository.save(compte);
    }
    public void deleteCompte(Long idCompte) {
        if (!compteRepository.existsById(idCompte)) {
            throw new ResourceNotFoundException("Compte with ID " + idCompte + " not found");
        }
        compteRepository.deleteById(idCompte);
    }
    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public Boolean isCompteActivated(String email){
        System.out.println("email" + email);
        Compte compte=compteRepository.findByEmail(email);
        System.out.println("compte "+compte);
        if (compte != null &&"Approuvé".equals(compte.getStatusCompteBacaire())) {

            return true; // Retourne la valeur de is_activated
        } else {
            return null; // Retourne null si le compte n'existe pas
        }    }
    public List<Compte> findByUserIdUser(Long idUser){
        System.out.println("Long idUser"+idUser);
       return compteRepository.findByUserIdUser(idUser);
    }
    public  List<Compte> findbyUser(User user){
        return compteRepository.findByUser(user);
    }

    public void save(Compte compte) {
        compteRepository.save(compte); // Sauvegarde le compte dans la base de données
    }

}
