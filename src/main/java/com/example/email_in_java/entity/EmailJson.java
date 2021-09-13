package com.example.email_in_java.entity;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EmailJson {

    Long id;
    String user;
    String sender;
    List<String> recipients;
    String subject;
    String body;
    String timestamp;
    boolean read;
    boolean archived;


    public EmailJson(Email email){
        this.id = email.getId();
        this.user = email.getUser().getEmail();
        this.sender = email.getSender().getEmail();
        recipients = new ArrayList<>();
        for(Odbiorca u: email.getRecipients()){
            recipients.add(u.getOdbiorca().getEmail());
        }

        this.subject = email.getSubject();
        this.body = email.getBody();
        this.timestamp = email.getTimestamp().toString();
        this.read = email.isRead();
        this.archived = email.isArchived();



    }
}
