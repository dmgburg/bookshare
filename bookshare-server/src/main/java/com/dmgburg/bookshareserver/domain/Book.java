package com.dmgburg.bookshareserver.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
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
    public Book() {
    }

    public Book(String name, String description, String author, String coverId) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.coverId = coverId;
    }

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

    public void setId(Long id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equal(id, book.id) &&
                Objects.equal(name, book.name) &&
                Objects.equal(description, book.description) &&
                Objects.equal(author, book.author) &&
                Objects.equal(owner, book.owner) &&
                Objects.equal(holder, book.holder) &&
                Objects.equal(coverId, book.coverId) &&
                Objects.equal(userQueue, book.userQueue) &&
                Objects.equal(notification, book.notification);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, description, author, owner, holder, coverId, userQueue, notification);
    }
}
