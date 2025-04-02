package com.banking.demo.repository;

import com.banking.demo.entity.Compte;
import com.banking.demo.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditRepository extends JpaRepository<Credit,Long> {
    List<Credit> findCreditByCompte(Compte compte);

}
