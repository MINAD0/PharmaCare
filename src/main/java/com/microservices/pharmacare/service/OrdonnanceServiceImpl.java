package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dto.OrdonnanceCreateDTO;
import com.microservices.pharmacare.dto.OrdonnanceDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdonnanceServiceImpl implements OrdonnanceService{

    private final OrdonnanceRepository ordonnanceRepository;
    private final PatientRepository patientRepository;

    public OrdonnanceServiceImpl(OrdonnanceRepository ordonnanceRepository, PatientRepository patientRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
        this.patientRepository = patientRepository;
    }
    @Override
    public OrdonnanceDTO createOrdonnance(OrdonnanceCreateDTO ordonnanceCreateDTO) {
        return null;
    }

    @Override
    public OrdonnanceDTO getOrdonnanceById(Long id) {
        return null;
    }

    @Override
    public List<OrdonnanceDTO> getAllOrdonnances() {
        return null;
    }

    @Override
    public List<OrdonnanceDTO> getOrdonnancesByPatientCode(String codePatient) {
        return null;
    }

    @Override
    public void deleteOrdonnance(Long id) {

    }
}
