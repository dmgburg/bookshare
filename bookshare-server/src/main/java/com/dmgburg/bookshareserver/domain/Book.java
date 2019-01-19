package com.dmgburg.bookshareserver.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "book_Sequence")
    @SequenceGenerator(name = "book_Sequence", sequenceName = "BOOK_SEQ")
    @JsonProperty("id")
    private Long id;

    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Lob
    @Type(type="text")
    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @Column(name = "author")
    @JsonProperty("author")
    private String author;

    @Column(name = "owner")
    @JsonProperty("owner")
    private String owner;

    @Column(name = "holder")
    @JsonProperty("holder")
    private String holder;

    @Column(name = "coverId")
    @JsonProperty("coverId")
    private String coverId;

    @JsonProperty("queue")
    @ElementCollection
    private List<String> userQueue;

    @OneToOne
    @JoinColumn(name = "NOTIFICATION_ID")
    @JsonProperty("notification")
    private Notification notification;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", coverId='" + coverId + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Book setHolder(String holder) {
        this.holder = holder;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public String getHolder() {
        return holder;
    }

    public Notification getNotification() {
        return notification;
    }

    public Book setNotification(Notification notification) {
        this.notification = notification;
        if (notification != null) {
            notification.setBook(this.getId());
        }
        return this;
    }

    public List<String> getUserQueue() {
        if (userQueue == null){
            userQueue = new ArrayList<>();
        }
        return userQueue;
    }
}
