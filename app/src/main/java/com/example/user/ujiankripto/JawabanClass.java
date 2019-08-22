package com.example.user.ujiankripto;

public class JawabanClass {

    int number;
    String encrypted;

    public JawabanClass() {
    }

    public JawabanClass(int number, String encrypted) {
        this.number = number;
        this.encrypted = encrypted;
    }

    public int getNumber() {
        return number;
    }

    public String getEncrypted() {
        return encrypted;
    }
}
