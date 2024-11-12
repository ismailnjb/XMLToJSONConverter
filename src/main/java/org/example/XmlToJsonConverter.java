package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;

public class XmlToJsonConverter {
    public static void main(String[] args) {
        String folderPath = "D:\\xmljsoncopies\\justXmlToJsonmultipe files\\src\\main\\java\\org\\example\\justxmltojsontest";

        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".xml"));

        if (files == null) {
            System.out.println("No XML files found in the directory.");
            return;
        }

        ObjectMapper jsonMapper = new ObjectMapper();
        XmlMapper xmlMapper = new XmlMapper();

        for (File xmlFile : files) {
            try {

                JsonNode jsonNode = xmlMapper.readTree(xmlFile);


                String jsonFileName = xmlFile.getName().replace(".xml", ".json");
                File jsonFile = new File(folderPath, jsonFileName);


                jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, jsonNode);

                System.out.println("Converted and saved JSON for " + xmlFile.getName() + " to " + jsonFile.getPath());

            } catch (IOException e) {
                System.err.println("Failed to convert file: " + xmlFile.getName());
                e.printStackTrace();
            }
        }
    }
}