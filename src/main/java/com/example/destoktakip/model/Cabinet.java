package com.example.destoktakip.model;

public enum Cabinet {
    A("Dolap A", true),
    B("Dolap B", true),
    C("Buzdolabı C", false);

    private final String displayName;
    private final boolean hasShelves;

    Cabinet(String displayName, boolean hasShelves) {
        this.displayName = displayName;
        this.hasShelves = hasShelves;
    }

    public boolean hasShelves() {
        return hasShelves;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getCode() {
        return this.name();
    }
}