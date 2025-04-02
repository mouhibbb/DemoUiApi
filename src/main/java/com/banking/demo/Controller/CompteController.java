package com.banking.demo.Controller;
import com.banking.demo.DTO.SetAmountDTO;
import com.banking.demo.DTO.TransactionRequestDTO;
import com.banking.demo.entity.*;
import com.banking.demo.repository.CompteRepository;
import com.banking.demo.repository.UserRepository;
import com.banking.demo.service.CompteService;
import com.banking.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4300")
@RestController
@RequestMapping("/api/compte")
public class CompteController {

    private final UserService userService ;

    private final CompteService compteService ;
    private static final Logger logger= (Logger) LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final CompteRepository compteRepository;

    public CompteController(CompteService compteService,
                            UserRepository userRepository,
                            UserService UserService,
                            CompteRepository compteRepository) {
        this.compteService = compteService;
        this.userRepository = userRepository;
        this.userService=UserService;
        this.compteRepository = compteRepository;
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
        System.out.println("email "+email);
        Map<String, Object> compteData = (Map<String, Object>) payload.get("compteData"); // Cast en Map

        // Trouver l'utilisateur associé à l'email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
        }

        // Ajouter l'utilisateur aux données du compte
        System.out.println("Données du compte après ajout de user_id : " + compteData);
        ObjectMapper objectMapper = new ObjectMapper();
        Compte compte = objectMapper.convertValue(compteData, Compte.class);
        compte.setUser(user);
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

    @GetMapping("/getList")
   // public ResponseEntity<List<Credit>> getUserCredits(@RequestParam String email) {

        public ResponseEntity<List<Compte>>getActivatedAccountbyEmail(@RequestParam String email){
        System.out.println("email "+email);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        else {
            User user=userService.findByEmail(email);
            List<Compte> comptesActive=compteRepository.findCompteByUserAndStatusCompteBacaire(user, StatusCompteBacaire.Approuvé);
            return ResponseEntity.ok(comptesActive);
        }
    }
    @PostMapping("/send")
    public ResponseEntity<?> transaction(@RequestBody TransactionRequestDTO requestDTO) {
        // Vérifier si les comptes existent
        Compte compteSource = compteRepository.findById(requestDTO.getIdCompteSource())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compte source introuvable"));
        Compte compteDestination = compteRepository.findById(requestDTO.getIdCompteCible())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compte cible introuvable"));
        Long montant = requestDTO.getMontant();

        // Vérification de montant valide
        if (montant <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le montant doit être supérieur à 0");
        }

        // Vérification du solde suffisant
        if (compteSource.getSolde() < montant) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solde insuffisant");
        }

        // Exécution de la transaction
        compteSource.setSolde(compteSource.getSolde() - montant);
        compteDestination.setSolde(compteDestination.getSolde() + montant);

        // Sauvegarde dans la base
        compteRepository.save(compteSource);
        compteRepository.save(compteDestination);

        return ResponseEntity.ok(Map.of("message", "Transaction effectuée avec succès"));
    }
    @PostMapping("/setAmount")
    public ResponseEntity<?> setAmount(@RequestBody SetAmountDTO amountDTO) {
        Long idCompte = amountDTO.getIdCompte();
        Long montant = amountDTO.getMontant();

        // 🔍 Validation de l'entrée
        if (idCompte == null || montant == null || montant < 0) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Données invalides : ID ou montant manquant/invalide"));
        }


        // 🔄 Récupération du compte
        Optional<Compte> optionalCompte = compteRepository.findById(idCompte);
        if (optionalCompte.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Compte introuvable avec l'ID : " + idCompte));
        }
        Compte compte = optionalCompte.get();


        // 💰 Mise à jour du solde
        compte.setSolde(compte.getSolde()+montant);
        compteRepository.save(compte);

        // ✅ Réponse de succès
        return ResponseEntity.ok(Map.of("message","Encaissement effectuer avec succes"));
    }
    @PostMapping("/removeAmount")
    public ResponseEntity<?> removeAmount(@RequestBody SetAmountDTO amountDTO) {
        Long idCompte = amountDTO.getIdCompte();
        Long montant = amountDTO.getMontant();

        // 🔍 Validation de l'entrée
        if (idCompte == null || montant == null || montant < 0) {
            return ResponseEntity.badRequest().body("Données invalides : ID ou montant manquant/invalide");
        }

        // 🔄 Récupération du compte
        Compte compte = compteRepository.findById(idCompte)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compte introuvable avec l'ID : " + idCompte
                ));
        if (compte.getSolde()<montant){
            return ResponseEntity.badRequest().body("Solde Insuffisant");
        }
        // 💰 Mise à jour du solde
        compte.setSolde(compte.getSolde()-montant);
        compteRepository.save(compte);

        // ✅ Réponse de succès
        return ResponseEntity.ok(Map.of("message","Décaissement effectuer avec succes"));
    }
//    @PostMapping("/urlAccounts")
//    public ResponseEntity<?> saveAccountsFromExcelFile(@RequestBody Map<String, String> payload){
//        String compteFilePath = payload.get("compteFilePath");
//        System.out.println("Chemin reçu : " + compteFilePath);
//        try (FileInputStream fis = new FileInputStream(compteFilePath);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//            Boolean isHeader=true;
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                if (isHeader){
//                    isHeader=false;
//                    continue;
//                }
//                Compte compte=new Compte();
//                compte.setCin((long) row.getCell(0).getNumericCellValue());
//                compte.setNom(row.getCell(5).getStringCellValue());
//                compte.setPrenom(row.getCell(6).getStringCellValue());
//                compte.setDate_naissance(row.getCell(2).getDateCellValue());
//                compte.setSexe(row.getCell(11).getStringCellValue());
//                compte.setNationalité(row.getCell(4).getStringCellValue());
//                compte.setRue(row.getCell(9).getStringCellValue());
//                compte.setVille(row.getCell(15).getStringCellValue());
//                compte.setCode_postal((int) row.getCell(1).getNumericCellValue());
//                compte.setPays(row.getCell(7).getStringCellValue());
//                compte.setTelephone((long) row.getCell(13).getNumericCellValue());
//                compte.setEmail(row.getCell(3).getStringCellValue());
//                compte.setProfession(row.getCell(8).getStringCellValue());
//                compte.setSalaire((int) row.getCell(10).getNumericCellValue());
//                compte.setStatus_emploi(row.getCell(12).getStringCellValue());
//                compte.setType_compte(row.getCell(14).getStringCellValue());
//                compte.setSolde(0L); // par défaut
//
//                compteService.save(compte);
//
//            }}
//
//        catch (DataIntegrityViolationException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(400).body(Map.of("message", "Un utilisateur avec cet email existe déjà."));
//        } catch (IOException | IllegalArgumentException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body(Map.of("message", "Erreur lors de la lecture ou de l'enregistrement."));
//        }
//
//        return ResponseEntity.ok(Map.of("message", "Fichier traité avec succès"));    }
@PostMapping("/urlAccounts")
public ResponseEntity<?> saveAccountsFromExcelFile(@RequestBody Map<String, String> payload) {
    String compteFilePath = payload.get("compteFilePath");
    System.out.println("Chemin reçu : " + compteFilePath);

    List<String> erreurs = new ArrayList<>();

    try (FileInputStream fis = new FileInputStream(compteFilePath);
         Workbook workbook = new XSSFWorkbook(fis)) {

        boolean isHeader = true;
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (isHeader) {
                isHeader = false;
                continue;
            }

            try {
                Compte compte = new Compte();
                compte.setCin((long) row.getCell(0).getNumericCellValue());
                compte.setNom(row.getCell(5).getStringCellValue());
                compte.setPrenom(row.getCell(6).getStringCellValue());
                compte.setDate_naissance(row.getCell(2).getDateCellValue());
                compte.setSexe(row.getCell(11).getStringCellValue());
                compte.setNationalité(row.getCell(4).getStringCellValue());
                compte.setRue(row.getCell(9).getStringCellValue());
                compte.setVille(row.getCell(15).getStringCellValue());
                compte.setCode_postal((int) row.getCell(1).getNumericCellValue());
                compte.setPays(row.getCell(7).getStringCellValue());
                compte.setTelephone((long) row.getCell(13).getNumericCellValue());
                compte.setEmail(row.getCell(3).getStringCellValue());
                compte.setProfession(row.getCell(8).getStringCellValue());
                compte.setSalaire((int) row.getCell(10).getNumericCellValue());
                compte.setStatus_emploi(row.getCell(12).getStringCellValue());
                compte.setType_compte(row.getCell(14).getStringCellValue());
                compte.setSolde(0L); // solde initial par défaut

                compteService.save(compte);

            } catch (DataIntegrityViolationException e) {
                String cin = String.valueOf((long) row.getCell(0).getNumericCellValue());
                erreurs.add("Compte avec CIN " + cin + " existe déjà.");
            } catch (Exception e) {
                erreurs.add("Erreur à la ligne " + row.getRowNum() + ": " + e.getMessage());
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of("message", "Erreur lors de la lecture du fichier."));
    }

    return ResponseEntity.ok(Map.of(
            "message", "Importation terminée",
            "erreurs", erreurs
    ));
}


}
