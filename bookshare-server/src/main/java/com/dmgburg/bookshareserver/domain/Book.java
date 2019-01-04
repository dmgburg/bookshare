package com.dmgburg.bookshareserver.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;

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

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getOwner() {
        return owner;
    }

    public String getHolder() {
        return holder;
    }
}
