package org.example;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // CSV to JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";

        try {
            List <Employee> csvList = parseCSV(columnMapping, csvFileName);
            String csvJson = listToJson(csvList);
            writeString(csvJson, "data.json");
            System.out.println("Файл data.json успешно создан.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // XML to JSON
        String xmlFileName = "data.xml";
        try {
            List <Employee> xmlList = parseXML(xmlFileName);
            String xmlJson = listToJson(xmlList);
            writeString(xmlJson, "data2.json");
            System.out.println("Файл data2.json успешно создан.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CSV Parsing
    public static List <Employee> parseCSV(String[] mapping, String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            ColumnPositionMappingStrategy <Employee> strategy = new ColumnPositionMappingStrategy <> ();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(mapping);

            CsvToBean <Employee> csvToBean = new CsvToBeanBuilder <Employee>(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(0)
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения CSV-файла", e);
        }
    }

    // XML Parsing
    public static List <Employee> parseXML(String xmlFile) {
        List <Employee> employees = new ArrayList <>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге XML файла", e);
        }

        return employees;
    }

    // JSON Conversion
    public static String listToJson(List <Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    // Write JSON to file
    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи JSON-файла", e);
        }
    }
}