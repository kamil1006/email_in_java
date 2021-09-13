package com.example.email_in_java.entity;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class Odbiorca {


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User odbiorca;

    public Odbiorca() {
    }

    public Odbiorca(User odbiorca) {
        this.odbiorca = odbiorca;

    }

    public User getOdbiorca() {
        return odbiorca;
    }

    public void setOdbiorca(User odbiorca) {
        this.odbiorca = odbiorca;
    }

    @Override
    public String toString() {
        return odbiorca.getEmail();
    }

    public String toJson() {
        return "{ recipient : " + odbiorca + '}';
    }

}
