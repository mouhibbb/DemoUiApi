package com.banking.demo.Controller;
import com.banking.demo.entity.Compte;
import com.banking.demo.entity.StatusCompteBacaire;
import com.banking.demo.entity.User;
import com.banking.demo.repository.UserRepository;
import com.banking.demo.service.CompteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4300")
@RestController
@RequestMapping("/api/compte")
public class CompteController {

    private final CompteService compteService ;
    private static final Logger logger= (Logger) LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public CompteController(CompteService compteService,
                            UserRepository userRepository) {
        this.compteService = compteService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Compte> getAllComptes() {
        return compteService.getAllComptes();}

    @GetMapping("/getnewAccount")
    public List<Compte> getNewComptes() {
        return compteService.getCompteEnCours();}
    @GetMapping("/{id}")
    public ResponseEntity<Compte> getCompteById(@PathVariable Long id) {
        logger.info("Request received for user ID: {}", id);
        try {
            Compte compte = compteService.getCompteById(id);
            return ResponseEntity.ok(compte);
        } catch (RuntimeException e) {
            logger.error("Error fetching user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/approuve/{id}")
    public void approuveClicked(@PathVariable("id") Long id){
        System.out.println("id"+id);
          Compte compte = compteService.getCompteById(id);
          compte.setStatusCompteBacaire(StatusCompteBacaire.Approuvé);
            compteService.save(compte);}
    @PutMapping("/encours/{id}")
    public void enCoursClicked(@PathVariable("id") Long id){
        System.out.println("id"+id);
        Compte compte = compteService.getCompteById(id);
        compte.setStatusCompteBacaire(StatusCompteBacaire.En_cours);
        compteService.save(compte);}

    @PutMapping("/refuse/{id}")
    public void refuseClicked(@PathVariable("id") Long id){
        Compte compte = compteService.getCompteById(id);
        compte.setStatusCompteBacaire(StatusCompteBacaire.Refusé);
        compteService.save(compte);}
    @PostMapping()
    public ResponseEntity<?> createCompte(@RequestBody Map<String, Object> payload) {
          // Récupérer l'email et les données du compte depuis le payload
        String email = (String) payload.get("email");
        System.out.println("email"+email);
        Map<String, Object> compteData = (Map<String, Object>) payload.get("compteData"); // Cast en Map

        // Trouver l'utilisateur associé à l'email
        User user = userRepository.findByEmail(email);
        System.out.println("user"+user);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
        }

        // Ajouter l'utilisateur aux données du compte
        System.out.println("Données du compte après ajout de user_id : " + compteData);
        ObjectMapper objectMapper = new ObjectMapper();
        Compte compte = objectMapper.convertValue(compteData, Compte.class);
        compte.setUser(user);
        System.out.println("compte "+compte);
        // Appeler le service (adapté pour accepter un Map)
         compteService.createCompte(compte);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Créé avec succès");
        return ResponseEntity.ok().body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Compte> updateCompte(@PathVariable("id") Long idCompte, @RequestBody Compte compteDetails) {
        return ResponseEntity.ok(compteService.updateCompte(idCompte, compteDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompte(@PathVariable("id") Long idCompte) {
        compteService.deleteCompte(idCompte);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/check")
    public ResponseEntity<?> checkIfCompteIsActivated(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        System.out.println("sout "+email);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("L'email est requis.");
        }

        try {
            Boolean isActivated = compteService.isCompteActivated(email);
            if (isActivated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
            }
            return ResponseEntity.ok(Map.of("isActivated", isActivated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la vérification.");
        }
    }
}
