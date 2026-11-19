package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.gymmick.smartgym.entity.Trainer;

public interface TrainerRepository extends JpaRepository<Trainer, UUID>, JpaSpecificationExecutor<Trainer> {

    Optional<Trainer> findById(UUID id);
}
