package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.repository.MedicamentRepository;
import com.microservices.pharmacare.dto.MedicamentCreateDTO;
import com.microservices.pharmacare.dto.MedicamentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicamentService {

    private final MedicamentRepository medicamentRepository;

    @Autowired
    public MedicamentService(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    public List<MedicamentDTO> getAllMedicaments() {
        return medicamentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public MedicamentDTO getMedicamentById(Long id) {
        Optional<Medicament> medicament = medicamentRepository.findById(id);
        return medicament.map(this::mapToDto).orElse(null);
    }

    public MedicamentDTO createMedicament(MedicamentCreateDTO dto) {
        Medicament medicament = new Medicament();
        medicament.setNom(dto.getNom());
        medicament.setDescription(dto.getDescription());

        // ✅ Handle image file safely
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            try {
                byte[] bytes = dto.getImageFile().getBytes();
                String base64Image = Base64.getEncoder().encodeToString(bytes);
                medicament.setImageBase64(base64Image); // 🔥 Store base64 image
            } catch (IOException e) {
                throw new RuntimeException("Failed to process image file", e);
            }
        } else {
            medicament.setImageBase64(null); // 🔥 Handle missing file
        }

        Medicament savedMedicament = medicamentRepository.save(medicament);
        return mapToDto(savedMedicament);
    }

    private MedicamentDTO mapToDto(Medicament medicament) {
        return new MedicamentDTO(medicament.getId(), medicament.getNom(), medicament.getDescription(), medicament.getImageBase64());
    }
}
