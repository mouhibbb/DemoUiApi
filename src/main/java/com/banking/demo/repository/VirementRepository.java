package com.banking.demo.repository;

import com.banking.demo.entity.Virement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirementRepository extends JpaRepository<Virement,Long> {

}
