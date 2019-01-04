package com.dmgburg.bookshareserver.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class UserInteraction {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "interaction_Sequence")
    @SequenceGenerator(name = "interaction_Sequence", sequenceName = "INTERACTION_SEQ")
    @Column(name = "id")
    @JsonProperty("id")
    private Long id;

    @Column(name = "interationType")
    @JsonProperty("interationType")
    private String interactionType;

    @Column(name = "fromUser")
    @JsonProperty("fromUser")
    private String fromUser;

    @Column(name = "to")
    @JsonProperty("to")
    private String toUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID")
    private Book book;

    @Override
    public String toString() {
        return "UserInteraction{" +
                "id=" + id +
                ", interactionType='" + interactionType + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", to='" + toUser + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
