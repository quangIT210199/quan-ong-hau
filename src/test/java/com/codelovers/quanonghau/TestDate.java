package com.codelovers.quanonghau;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

public class TestDate {

    @Test
    public void calculatorDate() {
        Calendar c = Calendar.getInstance();
        System.out.println(c.getTime());
        System.out.println(new Date(System.currentTimeMillis() + 86400));

        if (c.getTime().before(new Date(System.currentTimeMillis() + 86400))) {
            System.out.println("trước k");
        }
    }
}
