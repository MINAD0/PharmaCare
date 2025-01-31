package com.microservices.pharmacare.controller;

import com.microservices.pharmacare.dto.OrdonnanceDTO;
import com.microservices.pharmacare.service.OrdonnanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordonnances")
@CrossOrigin(origins = "*") // Permettre l'accès depuis n'importe quel domaine
public class OrdonnanceController {

    private final OrdonnanceService ordonnanceService;

    public OrdonnanceController(OrdonnanceService ordonnanceService) {
        this.ordonnanceService = ordonnanceService;
    }

    /**
     * Récupérer la liste de toutes les ordonnances.
     */
    @GetMapping
    public ResponseEntity<List<OrdonnanceDTO>> getAllOrdonnances() {
        List<OrdonnanceDTO> ordonnances = ordonnanceService.listOrdonnance();
        return ResponseEntity.ok(ordonnances);
    }

    /**
     * Ajouter une nouvelle ordonnance.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createOrdonnance(@RequestBody OrdonnanceDTO ordonnanceDTO) {
        try {
            ordonnanceService.createOrdonnance(ordonnanceDTO);
            return ResponseEntity.ok("Ordonnance créée avec succès !");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la création de l'ordonnance.");
        }
    }

    /**
     * Récupérer une ordonnance par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrdonnanceById(@PathVariable Long id) {
        try {
            OrdonnanceDTO ordonnance = ordonnanceService.getOrdonnanceById(id);
            return ResponseEntity.ok(ordonnance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer une ordonnance par son ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrdonnance(@PathVariable Long id) {
        try {
            ordonnanceService.deleteOrdonnance(id);
            return ResponseEntity.ok("Ordonnance supprimée avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la suppression de l'ordonnance.");
        }
    }
}
