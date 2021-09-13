package com.example.email_in_java.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name =  "email")
public class Email {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    User user;

    @ManyToOne
    User sender;
/*
    @ElementCollection(fetch = FetchType.EAGER, targetClass = User.class)
    @CollectionTable(
            name = "mail_recipients",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "odbiorca", nullable = false)
   private List<User> recipients;
*/
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "odbiorcy",
    joinColumns = @JoinColumn(name = "odbiorca_id"))
    List<Odbiorca> recipients;


    @Column
    String subject;

    @Column
    String body;

    @Column
    LocalDateTime timestamp;

    @Column
    boolean read;

    @Column
    boolean archived;

    public Email() {
    }

    public Email(User user, User sender, List<User> recipients, String subject, String body, LocalDateTime timestamp, boolean read, boolean archived) {
        this.user = user;
        this.sender = sender;
        this.recipients = konwertuj(recipients);
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
        this.read = read;
        this.archived = archived;
    }

    public List<Odbiorca> konwertuj(List<User> lista){

        List<Odbiorca> nowa= new ArrayList<>();
        for(User u:lista){
            nowa.add(new Odbiorca(u));
        }
        return nowa;
    }

    public void setRecipients(List<User> recipients) {
        this.recipients = konwertuj(recipients);
    }

    public String toJson(){

        return "{" +
                "id: " + id +
                ", user: " + user.getEmail() +
                ", sender: " + sender.getEmail() +
                ", recipients: " + recipients +
                ", subject: '" + subject + '\'' +
                ", body: '" + body + '\'' +
                ", timestamp: " + timestamp +
                ", read: " + read +
                ", archived: " + archived +
                '}';
    }


    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", user=" + user.getEmail() +
                ", sender=" + sender +
                ", recipients=" + recipients +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", timestamp=" + timestamp +
                ", read=" + read +
                ", archived=" + archived +
                '}';
    }


}

