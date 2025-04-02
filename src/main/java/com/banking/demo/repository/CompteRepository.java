package com.banking.demo.repository;
import com.banking.demo.entity.Compte;

import com.banking.demo.entity.StatusCompteBacaire;
import com.banking.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte,Long> {
    Compte findByEmail(String email);
    @Query("SELECT c FROM Compte c WHERE c.user.idUser = :idUser")
    List<Compte> findByUserIdUser(@Param("idUser") Long idUser);
    List<Compte> findByStatusCompteBacaire(StatusCompteBacaire status);
    List<Compte> findByUser(User user);
}
