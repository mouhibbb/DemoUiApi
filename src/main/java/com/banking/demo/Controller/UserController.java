package com.banking.demo.Controller;

import com.banking.demo.entity.Compte;
import com.banking.demo.entity.StatusCompteBacaire;
import com.banking.demo.entity.User;
import com.banking.demo.repository.UserRepository;
import com.banking.demo.service.CompteService;
import com.banking.demo.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public UserController(UserService userService, CompteService compteService){
        this.userService=userService;this.compteService=compteService;
    }

    private static final Logger logger= (Logger) LoggerFactory.getLogger(UserController.class);

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
}
