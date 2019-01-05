package com.dmgburg.bookshareserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

    @Column(name = "toUser")
    @JsonProperty("toUser")
    private String toUser;

    @OneToOne
    @JoinColumn(name = "BOOK_ID")
    @JsonProperty("book")
    private Book book;

    @Column(name = "state")
    @JsonProperty("state")
    private InteractionState state;

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

    public InteractionState getState() {
        return state;
    }

    public void setState(InteractionState state) {
        this.state = state;
    }
}
