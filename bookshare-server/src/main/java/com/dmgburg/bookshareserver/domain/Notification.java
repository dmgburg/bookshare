package com.dmgburg.bookshareserver.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.util.Objects;

@Entity
public class Notification {
    
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "notification_Sequence")
    @SequenceGenerator(name = "notification_Sequence", sequenceName = "NOTIFICATION_SEQ")
    @Column(name = "id")
    @JsonProperty("id")
    private Long id;

    @JsonProperty("book_id")
    private long bookId;

    @Column(name = "fromUser")
    @JsonProperty("fromUser")
    private String fromUser;

    @Column(name = "toUser")
    @JsonProperty("toUser")
    private String toUser;

    @Column(name = "type")
    @JsonProperty("type")
    private Type type;

    public long getBook() {
        return bookId;
    }

    public Notification setBook(long bookId) {
        this.bookId = bookId;
        return this;
    }

    public String getFromUser() {
        return fromUser;
    }

    public Notification setFromUser(String fromUser) {
        this.fromUser = fromUser;
        return this;
    }

    public String getToUser() {
        return toUser;
    }

    public Notification setToUser(String toUser) {
        this.toUser = toUser;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Notification setType(Type type) {
        this.type = type;
        return this;
    }

    public static enum Type{
        QUEUE_NOT_EMPTY, BOOK_IS_WAITING, OWNER_WANTS_THE_BOOK
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(bookId, that.bookId) &&
                Objects.equals(fromUser, that.fromUser) &&
                Objects.equals(toUser, that.toUser) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, fromUser, toUser, type);
    }
}
