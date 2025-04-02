package com.banking.demo.service;

import com.banking.demo.entity.Compte;
import com.banking.demo.entity.Role;
import com.banking.demo.entity.User;
import com.banking.demo.repository.UserRepository;
import org.hibernate.mapping.Any;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> getNonAdminUsers(){
        return userRepository.findByRoleNot(Role.Admin);
    }
    public User getUserById(Long idUser){
        return  userRepository.findById(idUser).orElse(null);
    }
    public User createUser(User user) {
        return userRepository.save(user);
    }
    public User getUserId(String email){
        System.out.println("email "+ email);
        System.out.println("userId "+userRepository.findByEmail(email));
        return userRepository.findByEmail(email);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getPassword() != null) {
            user.setPassword(userDetails.getPassword());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.isActive() != user.isActive()) { // Mettre à jour le statut actif si nécessaire
            user.setActive(userDetails.isActive());
        }
        if (userDetails.getRole() != null) { // Mettre à jour le statut actif si nécessaire
            user.setRole(userDetails.getRole());
        }

        if (userDetails.getComptes() != null) { // Mettre à jour le statut actif si nécessaire
            user.setComptes(userDetails.getComptes());
        }
        return userRepository.save(user);
    }
    public void deleteUser(Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new ResourceNotFoundException("User with ID " + idUser + " not found");
        }
        userRepository.deleteById(idUser);
    }
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public User findByEmail(String email) {
        System.out.println("mail"+userRepository.findByEmail(email));

        return userRepository.findByEmail(email);
        }
    public void save(User user) {
        userRepository.save(user);
    }

    public UserRepository.UserAccountProjection UserGetRandomUser(){
        return userRepository.findRandomUserAndAccount();}



}

