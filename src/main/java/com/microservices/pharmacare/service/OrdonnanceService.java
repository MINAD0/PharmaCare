package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.*;
import com.microservices.pharmacare.dao.repository.OrdonnanceDetailRepository;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.RappelRepository;
import com.microservices.pharmacare.dto.OrdonnanceDTO;
import com.microservices.pharmacare.dto.MedicamentDTO;
import com.microservices.pharmacare.dto.OrdonnanceDetailDTO;
import com.microservices.pharmacare.dto.PatientDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final OrdonnanceDetailRepository ordonnanceDetailRepository;
    private final RappelRepository rappelRepository;

    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository, OrdonnanceDetailRepository ordonnanceDetailRepository, RappelRepository rappelRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
        this.ordonnanceDetailRepository = ordonnanceDetailRepository;
        this.rappelRepository = rappelRepository;
    }

    public OrdonnanceDTO getOrdonnanceById(Long id) {
        Optional<Ordonnance> ordonnanceOptional = ordonnanceRepository.findById(id);

        if (ordonnanceOptional.isPresent()) {
            Ordonnance ordonnance = ordonnanceOptional.get();
            return new OrdonnanceDTO(
                    ordonnance.getId(),
                    ordonnance.getDescription(),
                    ordonnance.getDate(),
                    convertToPatientDTO(ordonnance.getPatient()),
                    ordonnance.getPharmacien(),
                    ordonnance.getMedicaments().stream()
                            .map(this::convertToMedicamentDTO)
                            .collect(Collectors.toList())
            );
        } else {
            throw new RuntimeException("Ordonnance non trouvée avec l'ID: " + id);
        }
    }

    public void deleteOrdonnance(Long id) {
        if (!ordonnanceRepository.existsById(id)) {
            throw new RuntimeException("Ordonnance non trouvée avec l'ID: " + id);
        }
        ordonnanceRepository.deleteById(id);
    }


    private List<Rappel> createRappelFromMedicament(MedicamentDTO medicamentDTO, Ordonnance ordonnance) {
        List<Rappel> rappels = new ArrayList<>();

        for (OrdonnanceDetailDTO detailDTO : medicamentDTO.getOrdonnanceDetail()) {
            Rappel rappel = Rappel.builder()
                    .titre("Rappel pour le médicament: " + medicamentDTO.getNom())
                    .description("Posologie: " + detailDTO.getPosologie() +
                            ", Fréquence: " + detailDTO.getFrequence())
                    .dateHeure(LocalDateTime.now().plusDays(1)) // Rappel pour le lendemain
                    .medicament(Medicament.builder().id(medicamentDTO.getId()).build())
                    .patient(ordonnance.getPatient())
                    .build();
            rappels.add(rappel);
        }

        return rappels;
    }

    public OrdonnanceDTO createOrdonnance(OrdonnanceDTO ordonnanceDTO) {
        if (ordonnanceDTO.getPatient() == null || ordonnanceDTO.getPharmacien() == null) {
            throw new IllegalArgumentException("Le patient et le pharmacien sont obligatoires pour créer une ordonnance.");
        }

        // Création de l'entité Ordonnance
        Ordonnance ordonnance = Ordonnance.builder()
                .date(ordonnanceDTO.getDate())
                .description(ordonnanceDTO.getDescription())
                .patient(Patient.builder().codePatient(ordonnanceDTO.getPatient().getCodePatient()).build())
                .pharmacien(Pharmacien.builder().id(ordonnanceDTO.getPharmacien().getId()).build())
                .build();

        // Sauvegarde en base
        ordonnance = ordonnanceRepository.save(ordonnance);

        // Traitement des détails d'ordonnance (médicaments, posologies, fréquences)
        Ordonnance finalOrdonnance = ordonnance;
        List<OrdonnanceDetail> prescriptionDetails = ordonnanceDTO.getMedicaments().stream()
                .flatMap(medicamentDTO -> medicamentDTO.getOrdonnanceDetail().stream()
                        .map(detailDTO -> OrdonnanceDetail.builder()
                                .posologie(detailDTO.getPosologie())
                                .frequence(detailDTO.getFrequence())
                                .medicament(Medicament.builder().id(medicamentDTO.getId()).build())
                                .ordonnance(finalOrdonnance)
                                .build()))
                .collect(Collectors.toList());

        // Enregistrement des détails de l'ordonnance
        ordonnanceDetailRepository.saveAll(prescriptionDetails);

        // Génération et sauvegarde des rappels
        List<Rappel> rappels = ordonnanceDTO.getMedicaments().stream()
                .flatMap(medicamentDTO -> createRappelFromMedicament(medicamentDTO, finalOrdonnance).stream())
                .collect(Collectors.toList());

        rappelRepository.saveAll(rappels);

        // 🔹 Conversion de l'ordonnance en DTO avant de la retourner
        return new OrdonnanceDTO(
                ordonnance.getId(),
                ordonnance.getDescription(),
                ordonnance.getDate(),
                ordonnanceDTO.getPatient(),
                ordonnanceDTO.getPharmacien(),
                ordonnanceDTO.getMedicaments()
        );
    }

    private PatientDTO convertToPatientDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        return new PatientDTO(
                patient.getCodePatient(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getTel(),
                patient.getCin(),
                patient.getProfilePictureUrl(),
                null, // Ordonnances non incluses pour éviter une boucle infinie
                patient.getCreatedAt(),
                patient.getUpdatedAt(),
                (patient.getMotDePasse() != null && !patient.getMotDePasse().isEmpty()) ? 1 : 0 // Status
        );
    }
    private MedicamentDTO convertToMedicamentDTO(Medicament medicament) {
        if (medicament == null) {
            return null;
        }

        // Récupérer les détails de l'ordonnance liés à ce médicament
        Optional<OrdonnanceDetail> optionalOrdonnanceDetail = ordonnanceDetailRepository.findByMedicament(medicament);

        List<OrdonnanceDetailDTO> ordonnanceDetailDTOs = new ArrayList<>();
        Long ordonnanceId = null;
        Long patientId = null;

        if (optionalOrdonnanceDetail.isPresent()) {
            OrdonnanceDetail detail = optionalOrdonnanceDetail.get();

            // Vérifier si le patient a un codePatient valide
            if (detail.getOrdonnance().getPatient().getCodePatient() != null) {
                patientId = Long.parseLong(detail.getOrdonnance().getPatient().getCodePatient());
            }

            ordonnanceId = detail.getOrdonnance().getId();

            // Construire le DTO de détail d'ordonnance avec 5 paramètres
            ordonnanceDetailDTOs.add(new OrdonnanceDetailDTO(
                    detail.getId(),
                    detail.getPosologie(),
                    detail.getFrequence(),
                    medicament.getId(), // Correction : ajout de `medicamentId`
                    ordonnanceId
            ));
        }

        // Retourner le DTO du médicament avec les détails associés
        return new MedicamentDTO(
                medicament.getId(),
                medicament.getNom(),
                medicament.getImage(),
                ordonnanceId,
                patientId,
                medicament.getRappels(),
                ordonnanceDetailDTOs // Liste des posologies et fréquences
        );
    }



    public List<OrdonnanceDTO> listOrdonnance() {
        try {
            return ordonnanceRepository.findAll().stream()
                    .map(ordonnance -> new OrdonnanceDTO(
                            ordonnance.getId(),
                            ordonnance.getDescription(),
                            ordonnance.getDate(),
                            convertToPatientDTO(ordonnance.getPatient()), // 🔹 Convertir ici
                            ordonnance.getPharmacien(),
                            ordonnance.getMedicaments().stream()
                                    .map(this::convertToMedicamentDTO)
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
