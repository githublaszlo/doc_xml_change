package org.example;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyFinderTest {

    private String text;
    private String text2;
    private String text3;
    private String text4;
    private ArrayList<String> keysInDocx;

    private KeyFinder kf;
    private KeyFinder kf2;


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        text = "Ez egy példa a <<reguláris kifejezés>> használatára a <<Jav<<aaaa<<a>> bbbbbb>>-ban.";
        text2 = "R<<övi>>d példa";
        text3 = "Ez egy példa a <[reguláris kifejezés]> használatára a <[Jav<[aaaa<[a]> bbbbbb]>-ban.";
        text4 = "R<[övi]>d példa";
        keysInDocx = new ArrayList<String>();
        keysInDocx.add("reguláris kifejezés");
        keysInDocx.add("a");
        keysInDocx.add("kifejezés");
        kf = new KeyFinder("<", ">");
        kf2 = new KeyFinder("\\[", "\\]");
    }

    @Test
    void startPosFindTest() {
        ArrayList<Integer> exceptedList1 = new ArrayList<>();
        ArrayList<Integer> exceptedList2 = new ArrayList<>();
        exceptedList1.add(2);
        assertArrayEquals(exceptedList1.toArray(), kf.startPosFind(text2, kf.match(text2)).toArray());
        assertArrayEquals(exceptedList1.toArray(), kf2.startPosFind(text4, kf2.match(text4)).toArray());
        exceptedList2.add(16);
        exceptedList2.add(66);
        assertArrayEquals(exceptedList2.toArray(), kf.startPosFind(text, kf.match(text)).toArray());
        assertArrayEquals(exceptedList2.toArray(), kf2.startPosFind(text3, kf2.match(text3)).toArray());
    }

    @Test
    void endPosFindTest() {
        ArrayList<Integer> exceptedList1 = new ArrayList<>();
        ArrayList<Integer> exceptedList2 = new ArrayList<>();
        exceptedList1.add(7);
        assertArrayEquals(exceptedList1.toArray(), kf.endPosFind(text2, kf.match(text2)).toArray());
        assertArrayEquals(exceptedList1.toArray(), kf2.endPosFind(text4, kf2.match(text4)).toArray());
        exceptedList2.add(37);
        exceptedList2.add(69);
        assertArrayEquals(exceptedList2.toArray(), kf.endPosFind(text, kf.match(text)).toArray());
        assertArrayEquals(exceptedList2.toArray(), kf2.endPosFind(text3, kf2.match(text3)).toArray());
    }

    @Test
    void matchTest() {
        ArrayList<String> exceptedList = new ArrayList<>();
        exceptedList.add("reguláris kifejezés");
        exceptedList.add("a");
        assertArrayEquals(exceptedList.toArray(), kf.match(text).toArray());
    }

    @Test
    void matchTestWithMap(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("reguláris kifejezés", "nem használja");
        ArrayList<String> exceptedList = new ArrayList<>();
        exceptedList.add("reguláris kifejezés");
        assertArrayEquals(exceptedList.toArray(), kf.match(text, map).toArray());
    }
}