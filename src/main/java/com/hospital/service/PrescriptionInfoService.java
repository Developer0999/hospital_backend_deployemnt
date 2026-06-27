package com.hospital.service;

import com.hospital.entity.PrescriptionInfo;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.PrescriptionInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PrescriptionInfoService {

    @Autowired
    private PrescriptionInfoRepository repo;
 @Autowired
    private HospitalRepository hospitalrepo;

    public Optional<PrescriptionInfo> findByDoctor(Long doctorId) {
        return repo.findByDoctorId(doctorId);
    }

    public PrescriptionInfo save(PrescriptionInfo info) {
        hospitalrepo.findById(info.getHospitalId()).ifPresent(h -> {
            // adapt to Hospital entity getter names
            info.setHospitalName(h.getHospitalName());
            info.setHospitalAddress(h.getAddress());
            info.setHospitalPhone(h.getContactNo());
            info.setHospitalEmail(h.getEmail());
          
        });
                       
        info.setUpdatedDate(LocalDate.now());
        return repo.save(info);
    }

    public PrescriptionInfo upsertByDoctor(Long doctorId, PrescriptionInfo info) {
        PrescriptionInfo existing = repo.findByDoctorId(doctorId).orElseGet(PrescriptionInfo::new);
        existing.setDoctorId(doctorId);
        existing.setHospitalId(info.getHospitalId());
        existing.setHospitalName(info.getHospitalName());
        existing.setHospitalAddress(info.getHospitalAddress());
        existing.setHospitalPhone(info.getHospitalPhone());
        existing.setHospitalEmail(info.getHospitalEmail());
        existing.setLocation(info.getLocation());
        existing.setRegNo(info.getRegNo());
        existing.setDefaultInstructions(info.getDefaultInstructions());
        existing.setDefaultFooter(info.getDefaultFooter());
        existing.setInstructionsJson(info.getInstructionsJson());
        existing.setFooterJson(info.getFooterJson());
        existing.setUpdatedDate(LocalDate.now());
        return repo.save(existing);
    }
}
