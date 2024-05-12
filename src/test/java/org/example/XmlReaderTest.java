package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class XmlReaderTest {

    XmlReader xmlReader1;
    XmlReader xmlReader2;

    @BeforeEach
    void setUp() {
        try {
            xmlReader1 = new XmlReader(new File("src/main/resources/test1.xml"));
            xmlReader2 = new XmlReader(new File("src/main/resources/test2.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void childItemKeysTest() {
        assertEquals("OOP", xmlReader1.childItemKeys().get("data2"));
        assertEquals("Váci út 193.", xmlReader2.childItemKeys().get("street_address"));
        assertEquals("Budapest", xmlReader2.childItemKeys().get("birthplace"));
    }

    @Test
    void childItemKeysTestNoArraychange() {
        assertNotEquals("OOP", xmlReader1.childItemKeys((LinkedHashMap) xmlReader1.getMap()).get("data2"));
        assertInstanceOf(ArrayList.class,
                ((LinkedHashMap) xmlReader1.
                        childItemKeys((LinkedHashMap) xmlReader1.getMap())).get("data"));
        ArrayList<String> lista = (ArrayList) (((LinkedHashMap) xmlReader1.
                childItemKeys((LinkedHashMap) xmlReader1.getMap())).get("data"));
        assertEquals("OOP", lista.get(1));

        assertEquals("Váci út 193.", xmlReader2.childItemKeys((LinkedHashMap) xmlReader2.getMap()).get("street_address"));
        assertEquals("Budapest", xmlReader2.childItemKeys((LinkedHashMap) xmlReader2.getMap()).get("birthplace"));
    }
}