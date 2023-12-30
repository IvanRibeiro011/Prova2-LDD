package org.example;


import javax.xml.stream.*;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MainQuestionD {
    public static void main(String[] args) {
        try {
            generateHtml("src/main/resources/transformado.xml", "src/main/resources/output.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateHtml(String inputFile, String outputFile) throws IOException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(inputFile));

        Map<String, Set<String>> countriesByYear = new HashMap<>();
        String currentCountry = null;
        String currentYear = null;

        while (reader.hasNext()) {
            int event = reader.next();

            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if ("record".equals(reader.getLocalName())) {
                        currentCountry = reader.getAttributeValue(null, "country");
                    } else if ("value".equals(reader.getLocalName())) {
                        currentYear = reader.getAttributeValue(null, "year");
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    if (currentCountry != null && currentYear != null) {
                        String population = reader.getText().trim();
                        if (!population.isEmpty()) {
                            long populationValue = Long.parseLong(population);
                            if (populationValue > 100000000) {
                                countriesByYear.computeIfAbsent(currentYear, k -> new HashSet<>()).add(currentCountry);
                            }
                        }
                    }
                    break;
            }
        }

        reader.close();

        // Ordenar anos em ordem crescente
        List<String> sortedYears = new ArrayList<>(countriesByYear.keySet());
        Collections.sort(sortedYears);

        // Escrever HTML usando XMLStreamWriter
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(fileWriter);

            writer.writeStartDocument();
            writer.writeCharacters("\n");
            writer.writeStartElement("html");
            writer.writeCharacters("\n\t");
            writer.writeStartElement("head");
            writer.writeCharacters("\n\t\t");
            writer.writeStartElement("title");
            writer.writeCharacters("Population Over 100 Million");
            writer.writeEndElement(); // title
            writer.writeCharacters("\n\t");
            writer.writeEndElement(); // head
            writer.writeCharacters("\n\t");
            writer.writeStartElement("body");

            for (String year : sortedYears) {
                Set<String> countries = countriesByYear.get(year);

                writer.writeCharacters("\n\t\t");
                writer.writeStartElement("ul");
                writer.writeAttribute("year", year);
                writer.writeAttribute("count", String.valueOf(countries.size()));

                for (String country : countries) {
                    writer.writeCharacters("\n\t\t\t");
                    writer.writeStartElement("li");
                    writer.writeCharacters(country);
                    writer.writeEndElement();
                }

                writer.writeCharacters("\n\t\t");
                writer.writeEndElement();
            }

            writer.writeCharacters("\n\t");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();
        }
    }
}