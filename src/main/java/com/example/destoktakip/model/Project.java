package com.example.destoktakip.model;

import static com.example.destoktakip.model.Isletme.*;

public enum Project {

    ISPIR_ALTIN_MADENI("İspir Madeni", ISPIR),
    ISPIR_BAKIR_SONDAJ("Dik Değirmen", ISPIR),
    DIVRIGI_GENEL_ARAMA("Divriği Genel Arama", DIVRIGI),

    KIRSEHIR_MERMER_OCAGI("Kırşehir Ocağı", KIRSEHIR);


    private final String displayName;
    private final Isletme isletme;

    Project(String displayName, Isletme isletme) {
        this.displayName = displayName;
        this.isletme = isletme;
    }

    public Isletme getIsletme() {
        return isletme;
    }

    @Override
    public String toString() {
        return displayName;
    }
}