package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JsonFileParser {
    public static void main(String[] args) {
        String inputFolderPath = "D:\\xmljsoncopies\\justXmlToJsonmultipe files\\src\\main\\java\\org\\example\\xmlfiles";
        String outputFilePath = "D:\\xmljsoncopies\\justXmlToJsonmultipe files\\src\\main\\java\\org\\example\\TransCnxt.json";

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode consolidatedArray = mapper.createArrayNode();
        AtomicInteger count44 = new AtomicInteger();
        try (Stream<Path> paths = Files.walk(Paths.get(inputFolderPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            JsonNode rootNode = mapper.readTree(path.toFile());
                            JsonNode transportContextArray = rootNode.get("TransportContext");
                            count44.getAndIncrement();
                            if (transportContextArray != null && transportContextArray.isArray()) {
                                int jsonobjreadcnt = 0;
                                for (JsonNode transportContext : transportContextArray) {
                                    ObjectNode consolidatedNode = mapper.createObjectNode();
                                    jsonobjreadcnt++;
                                    consolidatedNode.put("transportId", transportContext.get("TrnsprtId").asText());
                                    consolidatedNode.put("transportType", transportContext.get("TrnsprtTp").asText());
                                    consolidatedNode.put("scope", transportContext.get("Scp").asText());
                                    consolidatedNode.put("ctgy", transportContext.get("Ctgy").asText());

                                    // Extract fromURI
                                    String transportConfig = transportContext.get("TrnsprtCfgtn").asText();
                                    String fromURI = extractFromURI(transportConfig);
                                    consolidatedNode.put("fromURI", fromURI);


                                    String toURI = (extractToURI(transportConfig));
                                    consolidatedNode.put("toURI", toURI);

                                    String fromBean = getFromBean(fromURI);
                                    String toBean = getToBean(toURI);

                                    consolidatedNode.put("fromBean", fromBean);
                                    consolidatedNode.put("toBean", toBean);


                                    ObjectNode headersArray = extractHeaders(mapper, transportConfig);
                                    consolidatedNode.set("Headers", headersArray);

                                    consolidatedArray.add(consolidatedNode);
                                }
                                System.out.println(count44 + "." + path + " : " + jsonobjreadcnt);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });


            try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, consolidatedArray);
                System.out.println("Consolidated JSON written to " + outputFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(count44);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getToBean(String toURI) {

        Pattern toBeanPattern = Pattern.compile("^([a-zA-Z0-9-]+):");
        Matcher matcher = toBeanPattern.matcher(toURI);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();

        }

        return "";
    }

    private static String getFromBean(String fromURI) {

        Pattern fromBeanPattern = Pattern.compile("^([a-zA-Z0-9-]+):");
        Matcher matcher = fromBeanPattern.matcher(fromURI);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();

        }
        return "";
    }

    private static String extractFromURI(String config) {

        Pattern fromURIPattern = Pattern.compile("<from uri=\"([^\"]+?)(\\?[^\"]*)?\"/>");
        Matcher matcher = fromURIPattern.matcher(config);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String extractToURI(String config) {
        Pattern toURIPattern = Pattern.compile("<to uri=\"([^\"]+)\"/>");
        Matcher matcher = toURIPattern.matcher(config);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private static ObjectNode extractHeaders(ObjectMapper mapper, String config) {

        ObjectNode headersObject = mapper.createObjectNode();
        Pattern headerPattern = Pattern.compile("<setHeader name=\"([^\"]+)\">\\s*<constant>([^<]+)</constant>");
        Matcher matcher = headerPattern.matcher(config);

        while (matcher.find()) {
            headersObject.put(matcher.group(1), matcher.group(2));
        }


        Pattern simpleHeaderPattern = Pattern.compile("<setHeader name=\"([^\"]+)\">\\s*<simple>([^<]+)</simple>");
        Matcher simpleMatcher = simpleHeaderPattern.matcher(config);
        while (simpleMatcher.find()) {
            headersObject.put(simpleMatcher.group(1), simpleMatcher.group(2));
        }//n

        return headersObject;
    }
}
