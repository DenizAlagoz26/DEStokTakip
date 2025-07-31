package com.example.destoktakip.model;

public enum Isletme {
    ISPIR("İSPİR"),
    DIVRIGI("DİVRİĞİ"),
    KIRSEHIR("KIRŞEHİR");

    private final String displayName;

    Isletme(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}