package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Rappel;
import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.entities.Pharmacien;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.RappelRepository;
import com.microservices.pharmacare.dto.OrdonnanceDTO;
import com.microservices.pharmacare.dto.MedicamentDTO;
import com.microservices.pharmacare.dto.PatientDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final RappelRepository rappelRepository;

    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository, RappelRepository rappelRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
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



    public Ordonnance createOrdonnance(OrdonnanceDTO ordonnanceDTO) {
        // Vérifier que l'ordonnance a bien un patient et un pharmacien
        if (ordonnanceDTO.getPatient() == null || ordonnanceDTO.getPharmacien() == null) {
            throw new IllegalArgumentException("Le patient et le pharmacien sont obligatoires pour créer une ordonnance.");
        }

        // Construire l'entité Ordonnance
        Ordonnance ordonnance = Ordonnance.builder()
                .date(ordonnanceDTO.getDate())
                .description(ordonnanceDTO.getDescription())
                .patient(Patient.builder().codePatient(ordonnanceDTO.getPatient().getCodePatient()).build())
                .pharmacien(Pharmacien.builder().id(ordonnanceDTO.getPharmacien().getId()).build())
                .build();

        // Sauvegarder l'ordonnance en base
        ordonnance = ordonnanceRepository.save(ordonnance);

        // Créer et associer les rappels pour chaque médicament
        Ordonnance finalOrdonnance = ordonnance;
        List<Rappel> rappels = ordonnanceDTO.getMedicaments().stream()
                .map(medicamentDTO -> createRappelFromMedicament(medicamentDTO, finalOrdonnance))
                .collect(Collectors.toList());

        // Enregistrer les rappels
        rappelRepository.saveAll(rappels);

        return ordonnance;
    }

    private Rappel createRappelFromMedicament(MedicamentDTO medicamentDTO, Ordonnance ordonnance) {
        return Rappel.builder()
                .titre("Rappel pour le médicament: " + medicamentDTO.getNom())
                .description("Posologie: " + medicamentDTO.getPosologie() +
                        ", Fréquence: " + medicamentDTO.getFrequence())
                .dateHeure(LocalDateTime.now().plusDays(1)) // Rappel pour le lendemain
                .medicament(Medicament.builder().id(medicamentDTO.getId()).build())
                .patient(ordonnance.getPatient())
                .build();
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
        return new MedicamentDTO(
                medicament.getId(),
                medicament.getNom(),
                medicament.getPosologie(),
                medicament.getFrequence(),
                medicament.getImage(),
                medicament.getOrdonnance().getId(), // ✅ ID de l'ordonnance associée
                medicament.getOrdonnance().getPatient().getCodePatient() != null ?
                        Long.parseLong(medicament.getOrdonnance().getPatient().getCodePatient()) : null, // ✅ Convertir en Long
                medicament.getRappels()
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
