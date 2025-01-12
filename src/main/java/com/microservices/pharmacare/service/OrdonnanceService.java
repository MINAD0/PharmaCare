//package com.microservices.pharmacare.service;
//
//import com.microservices.pharmacare.dao.entities.Medicament;
//import com.microservices.pharmacare.dao.entities.Ordonnance;
//import com.microservices.pharmacare.dao.entities.Rappel;
//import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
//import com.microservices.pharmacare.dao.repository.RappelRepository;
//import com.microservices.pharmacare.dto.OrdonnanceDTO;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class OrdonnanceService {
//
//    private final OrdonnanceRepository ordonnanceRepository;
//    private final RappelRepository rappelRepository;
//
//    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository, RappelRepository rappelRepository) {
//        this.ordonnanceRepository = ordonnanceRepository;
//        this.rappelRepository = rappelRepository;
//    }
//
//    public Ordonnance createOrdonnance(OrdonnanceDTO ordonnanceDTO) {
//        // Create and save the ordonnance
//        Ordonnance ordonnance = Ordonnance.builder()
//                .date(ordonnanceDTO.getDate())
//                .description(ordonnanceDTO.getDescription())
//                .patient(ordonnanceDTO.getPatient())
//                .pharmacien(ordonnanceDTO.getPharmacien())
//                .build();
//
//        ordonnance = ordonnanceRepository.save(ordonnance);
//
//        // Automatically create reminders for each Medicament
//        List<Rappel> rappels = new ArrayList<>();
//        for (Medicament medicament : ordonnance.getMédicaments()) {
//            Rappel rappel = Rappel.builder()
//                    .titre("Rappel pour le médicament: " + medicament.getNom())
//                    .description("Posologie: " + medicament.getPosologie() +
//                            ", Fréquence: " + medicament.getFréquence())
//                    .dateHeure(LocalDateTime.now().plusDays(1)) // Set reminder for next day
//                    .medicament(medicament)
//                    .patient(ordonnance.getPatient())
//                    .build();
//
//            rappels.add(rappel);
//        }
//
//        // Save all reminders
//        rappelRepository.saveAll(rappels);
//
//        return ordonnance;
//    }
//}
