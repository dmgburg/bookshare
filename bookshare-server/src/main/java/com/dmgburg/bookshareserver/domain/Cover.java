package com.dmgburg.bookshareserver.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

@Entity
public class Cover {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "cover_Sequence")
    @SequenceGenerator(name = "cover_Sequence", sequenceName = "COVER_SEQ")
    @JsonProperty("id")
    private Long id;

    @JsonProperty("mediaType")
    private String mediaType;

    @JsonProperty("data")
    private byte[] data;

    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String filename) {
        this.mediaType = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
