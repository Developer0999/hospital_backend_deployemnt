package com.hospital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.entity.Medicine;
import com.hospital.repository.MedicineRepository;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository repo;

    public Medicine save(Medicine m) {
        if (m.getActive() == null) m.setActive(true);
        return repo.save(m);
    }

    public Medicine update(Long id, Medicine m) {
        Medicine ex = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found: " + id));
        ex.setMedicineName(m.getMedicineName());
        ex.setGenericName(m.getGenericName());
        ex.setCategory(m.getCategory());
        ex.setType(m.getType());
        ex.setManufacturer(m.getManufacturer());
        ex.setComposition(m.getComposition());
        ex.setPrice(m.getPrice());
        ex.setUnit(m.getUnit());
        if (m.getActive() != null) ex.setActive(m.getActive());
        return repo.save(ex);
    }

    public List<Medicine> findByHospital(Long hospitalId) {
        return repo.findByHospitalIdAndActiveTrue(hospitalId);
    }

    public List<Medicine> search(Long hospitalId, String name) {
        return repo.findByHospitalIdAndMedicineNameContainingIgnoreCaseAndActiveTrue(hospitalId, name);
    }

    public List<Medicine> findByCategory(Long hospitalId, String category) {
        return repo.findByHospitalIdAndCategoryAndActiveTrue(hospitalId, category);
    }

    public Optional<Medicine> findById(Long id) {
        return repo.findById(id);
    }

    public void delete(Long id) {
        repo.findById(id).ifPresent(m -> {
            m.setActive(false);
            repo.save(m);
        });
    }

    public void hardDelete(Long id) {
        repo.deleteById(id);
    }
}
