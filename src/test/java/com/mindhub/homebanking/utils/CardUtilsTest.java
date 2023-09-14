package com.mindhub.homebanking.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
@SpringBootTest
class CardUtilsTest {

    @Test
    void getCvv() {
        Random random = new Random();
        int cvv = CardUtils.getCvv(random);
        assertThat(cvv, allOf(greaterThanOrEqualTo(1), lessThanOrEqualTo(999)));
    }

    @Test
    void getCardNumber() {
        String numberCard = CardUtils.getCardNumber();
        assertThat(numberCard, not(isEmptyOrNullString()));
    }
}