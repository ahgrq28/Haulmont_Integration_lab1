package com.example.testapplication.service.impl;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component("PasswordGenerator")
public class PasswordGenerator {

    private final static byte[] ALL_SYMBOLS = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
    private final static String REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    public String generate() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        while (!result.toString().matches(REGEX)) {
            result.reset();
            for (int i = 0; i < 8; i++) {
                int random = (int) (ALL_SYMBOLS.length * Math.random());
                result.write(ALL_SYMBOLS[random]);
            }
        }
        return result.toString();
    }
}
