package org.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateHandlerTest {

    @Test
    void isoToStringTest(){
        assertEquals("1989.03.15.", DateHandler.isoToString("1989-03-15T11:07:14.561Z"));
    }

    @Test
    void localDateToStringTest(){
        LocalDateTime date = LocalDateTime.of(2024,5,1,15,5,1);
        assertEquals("2024.05.01.", DateHandler.localDateToString(date));
    }
}