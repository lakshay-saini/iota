package com.example.iota;

import java.util.Random;

public class IOTASEEDGenerator {


    static final String TRYTE_ALPHABET = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final int SEED_LEN = 81;

    public static String getIOTASeed() {

        Random random = new Random();
        // the resulting seed
        StringBuilder seed = new StringBuilder(SEED_LEN);
        for(int i = 0; i < SEED_LEN; i++) {
            int n = random.nextInt(27);
            char c = TRYTE_ALPHABET.charAt(n);
            seed.append(c);
        }
        return seed.toString();
    }

}
