package com.example.email_in_java.service;

import com.example.email_in_java.entity.Email;

import com.example.email_in_java.entity.Odbiorca;
import com.example.email_in_java.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;


@Repository
@Transactional
public class EmailService {
    //-------------------------------------------------------------------------------------------------------
    @Autowired
    EntityManager em;
    //-------------------------------------------------------------------------------------------------------
    public List<Email> findAllByRecipientArchivedFalse(User recipient){

        List<Email> resultList = em.createQuery("SELECT c FROM Email c WHERE c.archived = false").getResultList();

        ListIterator<Email>
                iterator = resultList.listIterator();

        while (iterator.hasNext()) {
            User user = iterator.next().getUser();
            boolean found = false;
            if(!user.equals(recipient)){
                iterator.remove();
            }
        }

        iterator = resultList.listIterator();

        while (iterator.hasNext()) {
            List<Odbiorca> recipients = iterator.next().getRecipients();


            boolean found = false;
            for( Odbiorca o: recipients){
                if (o.getOdbiorca().equals(recipient) ){
                    found = true;
                }
            }
           if(!found){
               iterator.remove();
           }

        }

        int kk=0;

        return resultList;
    }
    //-------------------------------------------------------------------------------------------------------
    public List<Email> findAllByRecipientArchivedTrue(User recipient){

        List<Email> resultList = em.createQuery("SELECT c FROM Email c WHERE c.archived = true").getResultList();

        ListIterator<Email>
                iterator = resultList.listIterator();

        while (iterator.hasNext()) {
            User user = iterator.next().getUser();
            boolean found = false;
            if(!user.equals(recipient)){
                iterator.remove();
            }
        }

                iterator = resultList.listIterator();
        while (iterator.hasNext()) {
            List<Odbiorca> recipients = iterator.next().getRecipients();

            boolean found = false;
            for( Odbiorca o: recipients){
                if (o.getOdbiorca().equals(recipient) ){
                    found = true;
                }
            }
            if(!found){
                iterator.remove();
            }

        }

        int kk=0;

        return resultList;
    }
    //-------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------
}
