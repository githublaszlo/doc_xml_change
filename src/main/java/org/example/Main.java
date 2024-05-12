package org.example;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class Main {

    public static void main(String[] args) throws IOException {

        LinkedHashMap<String, Object> change;

        String pathOriginal = "src/main/resources/"; // path template
        String templateDoc = "docman_test.docx"; // original document will not be changed

        XmlReader reader = new XmlReader(new File("src/main/resources/docman_test.xml"));

        change = reader.childItemKeys();
        change.replace("birthday", DateHandler.isoToString((String) change.get("birthday")));
        addActDate(change);
        addSelector(change);
        WordHandler.changeDocx(change, pathOriginal, templateDoc);
    }

    private static void addSelector(LinkedHashMap<String, Object> change) {
        String selector = "folyamatban/felvéve/elutasítva";
        change.put( selector , (selector.split("/"))[Integer.parseInt((String) change.get("customer_type"))-1]);
    }

    private static void addActDate(LinkedHashMap<String, Object> change) {
        change.put("dátum", DateHandler.localDateToString(LocalDateTime.now()));
    }

}