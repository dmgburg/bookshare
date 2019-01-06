package com.dmgburg.bookshareserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Collections;

@Entity
public class User implements UserDetails {

    @Id
    @Column(name = "email", nullable = false)
    @JsonProperty("email")
    private String email;

    @Column(name = "passwordHash")
    @JsonProperty("passwordHash")
    private String passwordHash;

    @Column(name = "passwordSalt")
    @JsonProperty("passwordSalt")
    private String passwordSalt;

    @Column(name = "confirmationPending")
    @JsonProperty("confirmationPending")
    private String confirmationPending;

    @Column(name = "confirmationPendingSentAt")
    @JsonProperty("confirmationPendingSentAt")
    private Long confirmationPendingSentAt;

    public String getEmail() {
        return email;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", passwordHash=" + passwordHash +
                '}';
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return "{noop}"+passwordHash;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    public String getConfirmationPending() {
        return confirmationPending;
    }

    public void setConfirmationPending(String confirmationPending) {
        this.confirmationPending = confirmationPending;
    }

    public Long getConfirmationPendingSentAt() {
        return confirmationPendingSentAt;
    }

    public void setConfirmationPendingSentAt(Long confirmationPendingSentAt) {
        this.confirmationPendingSentAt = confirmationPendingSentAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
