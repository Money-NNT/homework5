package com.example.homeword5;

import java.io.Serializable;

public class Photo implements Serializable {
    private final long id;
    private final String title;
    private final String description;
    private final byte[] image;

    public Photo(long id, String title, String description, byte[] image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public Photo(String title, String description, byte[] image) {
        this.id = -1; // Temp ID for new photos
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public byte[] getImage() { return image; }
}