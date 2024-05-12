package org.example;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class XmlReader {

    Map<String, Object> map;

    public XmlReader(File xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        map = xmlMapper.readValue(xml, Map.class);
    }


    public LinkedHashMap childItemKeys() {
        return arrayCountList(childItemKeys((LinkedHashMap<String, Object>) map));
    }

    public LinkedHashMap childItemKeys(LinkedHashMap<String, Object> map) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            if (isValueLinkedHashMap(map.get(key))) {
                LinkedHashMap<String, Object> tempresult = childItemKeys((LinkedHashMap<String, Object>) map.get(key));
                result.putAll(tempresult);
            } else {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

    private boolean isValueLinkedHashMap(Object xml) {
        return xml instanceof LinkedHashMap;
    }

    private LinkedHashMap arrayCountList(LinkedHashMap<String, Object> map) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) instanceof ArrayList &&
                    ((ArrayList) map.get(key)).get(0) instanceof String) {
                ArrayList<String> stringList = (ArrayList<String>) map.get(key);
                for (int i = 0; i < stringList.size(); i++) {
                    result.put(key + (i + 1), stringList.get(i));
                }
            } else {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

    public Map<String, Object> getMap() {
        return map;
    }
}
