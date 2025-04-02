package com.banking.demo.Controller;

import com.banking.demo.entity.*;
import com.banking.demo.service.CompteService;
import com.banking.demo.service.CreditService;
import com.banking.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4300")
@RestController
@RequestMapping("/api/credits")
public class CreditController {


    private final CreditService creditService;
    private final UserService userService;
    private final CompteService compteService;
    public CreditController(CreditService creditService,UserService userService,CompteService compteService) {
        this.creditService = creditService;
        this.userService=userService;
        this.compteService=compteService;

    }
    private static final Logger logger= (Logger) LoggerFactory.getLogger(CreditController.class);

    @PostMapping
    public ResponseEntity<?> createCredit(@RequestBody Map<String, Object> payload) {
            System.out.println(payload);
        String email = (String) payload.get("email");
        String idCompte=(String) payload.get("idCompte");
        Long id_Compte=Long.parseLong(idCompte);
        Map<String, Object> creditData = (Map<String, Object>) payload.get("creditData"); // Cast en Map
        User user=userService.findByEmail(email);
        System.out.println("user+"+user);

        Set<Compte> comptes=user.getComptes();
        Compte compte=comptes.stream().filter(com -> com.getIdCompte().equals(id_Compte.longValue())).findFirst().orElseThrow(()->new RuntimeException("Compte non trouver par l'utilisateur"));
        System.out.println("compte "+compte);
        ObjectMapper objectMapper = new ObjectMapper();
        Credit credit= objectMapper.convertValue(creditData,Credit.class);
        credit.setCompte(compte);
        System.out.println(credit);
        Credit createdCredit = creditService.createCredit(credit);
        return new ResponseEntity<>(createdCredit, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<Credit>> getAllCredits() {
        List<Credit> credits = creditService.getAllCredit();
        return new ResponseEntity<>(credits, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Credit> getCreditById(@PathVariable("id") Long idCredit) {
        logger.info("Request received for Credit ID: {}", idCredit);
        try {
            Credit credit = creditService.getCreditById(idCredit);
            return ResponseEntity.ok(credit);
        } catch (RuntimeException e) {
            logger.error("Error fetching Credit with ID: {}", idCredit, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Endpoint pour mettre à jour un crédit
    @PutMapping("/{id}")
    public ResponseEntity<Credit> updateCredit(@PathVariable Long id, @RequestBody Credit credit) {
        Credit updatedCredit = creditService.updateCredit(id, credit);
        return ResponseEntity.ok(updatedCredit);
    }
    @GetMapping("/userCredit")
    public ResponseEntity<List<Credit>> getUserCredits(@RequestParam String email) {
        User user=userService.findByEmail(email);
        List<Compte> comptes=compteService.findbyUser(user);
        List<Credit> credits = comptes.stream().flatMap(compte -> creditService.findCreditbycompte(compte).stream())
                .collect(Collectors.toList());
        return ResponseEntity.ok(credits);
    }
    // Endpoint pour supprimer un crédit
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredit(@PathVariable Long id) {
        creditService.deleteCredit(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/approuve/{id}")
    public void approuveClicked(@PathVariable("id") Long id){
        System.out.println("id"+id);
        Credit credit = creditService.getCreditById(id);
        credit.setStatusCredit(StatusCredit.Approuvé);
        creditService.save(credit);}
    @PutMapping("/encours/{id}")
    public void enCoursClicked(@PathVariable("id") Long id){
        Credit credit = creditService.getCreditById(id);
        credit.setStatusCredit(StatusCredit.En_cours);
        creditService.save(credit);}

    @PutMapping("/refuse/{id}")
    public void refuseClicked(@PathVariable("id") Long id){
        Credit credit = creditService.getCreditById(id);
        credit.setStatusCredit(StatusCredit.Refusé);
        creditService.save(credit);}
}
