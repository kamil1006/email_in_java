package com.example.email_in_java.repository;

import com.example.email_in_java.entity.Email;
import com.example.email_in_java.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    //List<Email> findAllBySender(User sender);

    @Query(value = "SELECT c FROM Email c WHERE c.user = ?1 AND c.sender = ?2")
    List<Email> findAllByUserBySender(User user, User sender);

    //Email findById(long id);
/*
    @Query(value = "SELECT c FROM Email c WHERE c.archived = true AND c.sender = ?1")
    List<Email> findAllBySenderArchivedTrue(User sender);
  */



}
