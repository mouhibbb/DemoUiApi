package com.banking.demo.Controller;

import com.banking.demo.entity.Virement;
import com.banking.demo.service.UserService;
import com.banking.demo.service.VirementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/virement")
public class VirementController {
    private static final Logger logger= (Logger) LoggerFactory.getLogger(UserController.class);

    private final VirementService virementService;
    public VirementController(VirementService virementService){
        this.virementService=virementService;
    }

    // Récupérer toutes les Virements
    @GetMapping
    public List<Virement> getAllVirements() {
        return virementService.getAllVirements();
    }

    // Récupérer une Virement par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Virement> getVirementById(@PathVariable Long id) {
        logger.info("Request received for user ID: {}", id);
        try {
            Virement virement = virementService.getVirementById(id);
            return ResponseEntity.ok(virement);
        } catch (RuntimeException e) {
            logger.error("Error fetching user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Créer une nouvelle Virement
    @PostMapping
    public Virement createVirement(@RequestBody Virement virement) {
        return virementService.createVirement(virement);
    }

    // Mettre à jour une Virement existante
    @PutMapping("/{id}")
    public ResponseEntity<Virement> updateVirement(@PathVariable("id") Long id, @RequestBody Virement virementDetails) {
        return ResponseEntity.ok(virementService.updateVirement(id, virementDetails));
    }

    // Supprimer une Virement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVirement(@PathVariable("id") Long idVirement) {
        virementService.deleteVirement(idVirement);
        return ResponseEntity.noContent().build();
    }
}