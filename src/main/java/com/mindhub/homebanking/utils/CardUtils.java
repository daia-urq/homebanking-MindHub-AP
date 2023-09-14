package com.mindhub.homebanking.utils;



import java.util.Random;

public final class CardUtils {

    private CardUtils() {
    }


    public static int getCvv(Random random) {
        int cvv = random.nextInt(999) + 1;
        return cvv;
    }


    public static String getCardNumber() {
        Random random = new Random();
        String finalCardNumber;
        StringBuilder cardNumber = new StringBuilder(16);

        for (int j = 0; j < 4; j++) {
            int fourDigit = random.nextInt(9999) + 1;
            String numbers = String.format("%04d", fourDigit);
            cardNumber.append(numbers);
            if (j < 3) {
                cardNumber.append(" ");
            }
        }
        finalCardNumber = cardNumber.toString();
        return finalCardNumber;
    }
}
