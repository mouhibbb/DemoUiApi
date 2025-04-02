package com.banking.demo.Controller;

import com.banking.demo.entity.Compte;
import com.banking.demo.entity.Role;
import com.banking.demo.entity.StatusCompteBacaire;
import com.banking.demo.entity.User;
import com.banking.demo.repository.UserRepository;
import com.banking.demo.service.CompteService;
import com.banking.demo.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
@RestController
@CrossOrigin(origins = "http://localhost:4300")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CompteService compteService;

    public UserController(UserService userService, CompteService compteService,
                          UserRepository userRepository){
        this.userService=userService;this.compteService=compteService;
        this.userRepository = userRepository;
    }

    private static final Logger logger= (Logger) LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers()  {
        return userService.getNonAdminUsers();
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Request received for user ID: {}", id);
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            logger.error("Error fetching user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PutMapping("/active/{id}")
    public void activeClicked(@PathVariable("id") Long id){
        System.out.println("id"+id);
        User user = userService.getUserById(id);
        user.setActive(true);
        userService.save(user);}
    @PutMapping("/desactive/{id}")
    public void desactiveClicked(@PathVariable("id") Long id){
        System.out.println("id"+id);
        User user = userService.getUserById(id);
        user.setActive(false);
        userService.save(user);}

    @PostMapping
    public User createCustomer(@RequestBody User user) {

        System.out.println("user"+user);
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long idUser) {
        userService.deleteUser(idUser);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/usercheck")
    public ResponseEntity<Long> searchUser(@RequestBody String email) {
        email = email.replace("\"", ""); // Supprime les guillemets doubles si présents
        System.out.println("Email reçu1 : " + email);
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retourne une erreur 404 si l'utilisateur n'existe pas
        }
        System.out.println(user);
        return ResponseEntity.ok(user.getIdUser());
    }
    @PostMapping("/compte")
    public Set<Compte> searchUserCompte(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        email = email.replace("\"", ""); // Supprime les guillemets doubles si présents
        Set<Compte> comptes = userService.findByEmail(email).getComptes();
        System.out.println("COMPTES ;"+ comptes);
        return comptes;


    }
    @GetMapping("getRandomUser")
    public ResponseEntity<UserRepository.UserAccountProjection> getRandomUser(){
        UserRepository.UserAccountProjection userAccount=userService.UserGetRandomUser();

        return ResponseEntity.ok(userAccount);
    }
//    @PostMapping("/urlUsers")
//    public ResponseEntity<?> saveUsersFromExcelFile(@RequestBody Map<String, String> payload){
//        String urlExcelFile = payload.get("urlExcelFile");
//        System.out.println("Chemin reçu : " + urlExcelFile);
//        try (FileInputStream fis = new FileInputStream(urlExcelFile);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//            Boolean isHeader=true;
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                if (isHeader){
//                    isHeader=false;
//                    continue;
//                }
//                User user=new User();
//                user.setLastName(userService.getStringCellValue(row.getCell(0)));
//                user.setFirstName(userService.getStringCellValue(row.getCell(1)));
//                user.setEmail(userService.getStringCellValue(row.getCell(2)));
//                user.setPassword(userService.getStringCellValue(row.getCell(3)));
//                // Conversion texte -> enum
//                String roleStr = userService.getStringCellValue(row.getCell(4));
//                Role role = userService.parseRole(roleStr);
//                System.out.println(role);
//               // user.setRole(role);
//                System.out.println(role);
//                userRepository.save(user);
//
//            }}
//
//            catch (DataIntegrityViolationException e) {
//                e.printStackTrace();
//                return ResponseEntity.status(400).body(Map.of("message", "Un utilisateur avec cet email existe déjà."));
//            } catch (IOException | IllegalArgumentException e) {
//                e.printStackTrace();
//                return ResponseEntity.status(500).body(Map.of("message", "Erreur lors de la lecture ou de l'enregistrement."));
//            }
//
//            return ResponseEntity.ok(Map.of("message", "Fichier traité avec succès"));    }

    @PostMapping("/urlUsers")
    public ResponseEntity<?> saveUsersFromExcelFile(@RequestBody Map<String, String> payload) {
        String urlExcelFile = payload.get("urlExcelFile");
        System.out.println("Chemin reçu : " + urlExcelFile);

        List<String> erreurs = new ArrayList<>();
        int ligne = 1;

        try (FileInputStream fis = new FileInputStream(urlExcelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            boolean isHeader = true;
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                ligne++;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                try {
                    User user = new User();
                    user.setLastName(userService.getStringCellValue(row.getCell(0)));
                    user.setFirstName(userService.getStringCellValue(row.getCell(1)));
                    user.setEmail(userService.getStringCellValue(row.getCell(2)));
                    user.setPassword(userService.getStringCellValue(row.getCell(3)));

                    // Conversion texte -> enum
                    String roleStr = userService.getStringCellValue(row.getCell(4));
                    Role role = userService.parseRole(roleStr);
                    System.out.println(role);

                    // user.setRole(role); // décommente si nécessaire
                    userRepository.save(user);

                } catch (DataIntegrityViolationException ex) {
                    erreurs.add("Ligne " + ligne + ": email déjà utilisé -> " + userService.getStringCellValue(row.getCell(2)));
                } catch (Exception ex) {
                    erreurs.add("Ligne " + ligne + ": erreur inconnue.");
                    ex.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Erreur de lecture du fichier."));
        }

        if (!erreurs.isEmpty()) {
            return ResponseEntity.status(207).body(Map.of(
                    "message", "Fichier partiellement traité avec erreurs.",
                    "erreurs", erreurs
            ));
        }
        return ResponseEntity.ok(Map.of("message", "Fichier traité avec succès sans erreurs."));
    }



}
