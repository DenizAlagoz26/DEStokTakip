package com.example.destoktakip.model;

public enum Shelf {
    S1("1"),
    S2("2"),
    S3("3"),
    S4("4");

    private final String displayName;

    Shelf(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getCode() {
        return this.name();
    }
}