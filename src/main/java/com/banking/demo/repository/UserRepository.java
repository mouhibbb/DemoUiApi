package com.banking.demo.repository;

import com.banking.demo.entity.Compte;
import com.banking.demo.entity.Role;
import com.banking.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    List <User> findByRoleNot(Role role);
    public interface UserAccountProjection{
        Long getIdCompte();
        String getEmail();
    }
/*    @Query(value = "select id_user,email from _user ORDER BY RANDOM() LIMIT 1",nativeQuery = true)
    UserProjection findRandomUser();

    @Query("select c from _compte_bancaire c where c.user_id=:userId")
    List<Compte> findAccountByUserId(Long userId);*/

    @Query("SELECT u.email AS email, c.id AS idCompte " +
            "FROM User u JOIN u.comptes c " +
            "ORDER BY RANDOM() limit 1")
    UserAccountProjection findRandomUserAndAccount();

}
