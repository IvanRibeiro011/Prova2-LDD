package org.example;

import javax.xml.stream.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

public class MainQuestionA {
    public static void main(String[] args) {
        try {
            String inputFile = "src/main/resources/population.xml";
            String outputFile = "src/main/resources/transformado.xml";

            Reader reader = new FileReader(inputFile);
            Writer writer = new FileWriter(outputFile);

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlReader = inputFactory.createXMLStreamReader(reader);

            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlWriter.writeCharacters("\n");

            Map<String, Map<String, String>> populations = new TreeMap<>();

            while (xmlReader.hasNext()) {
                int event = xmlReader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        if ("record".equals(xmlReader.getLocalName())) {
                            processRecord(xmlReader, populations);
                        }
                        break;
                }
            }

            xmlWriter.writeStartElement("population");
            xmlWriter.writeCharacters("\n");

            for (Map.Entry<String, Map<String, String>> entry : populations.entrySet()) {
                xmlWriter.writeCharacters("  ");
                xmlWriter.writeStartElement("record");
                xmlWriter.writeAttribute("country", entry.getKey());
                xmlWriter.writeCharacters("\n");

                Map<String, String> values = entry.getValue();
                for (Map.Entry<String, String> valueEntry : values.entrySet()) {
                    xmlWriter.writeCharacters("    ");
                    xmlWriter.writeStartElement("value");
                    xmlWriter.writeAttribute("year", valueEntry.getKey());
                    xmlWriter.writeCharacters(valueEntry.getValue());
                    xmlWriter.writeEndElement();
                    xmlWriter.writeCharacters("\n");
                }

                xmlWriter.writeCharacters("  ");
                xmlWriter.writeEndElement();
                xmlWriter.writeCharacters("\n");
            }

            xmlWriter.writeEndElement();
            xmlWriter.writeCharacters("\n");

            xmlReader.close();
            xmlWriter.close();

            System.out.println("Transformação concluída com sucesso.");

        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processRecord(XMLStreamReader xmlReader, Map<String, Map<String, String>> populations) throws XMLStreamException {
        String country = null;
        String year = null;
        String value = null;

        while (xmlReader.hasNext()) {
            int event = xmlReader.next();

            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if ("field".equals(xmlReader.getLocalName())) {
                        String fieldName = xmlReader.getAttributeValue(null, "name");
                        String fieldValue = xmlReader.getElementText();

                        if ("Country".equals(fieldName)) {
                            country = fieldValue;
                        } else if ("Year".equals(fieldName)) {
                            year = fieldValue;
                        } else if ("Value".equals(fieldName)) {
                            value = fieldValue;
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if ("record".equals(xmlReader.getLocalName())) {
                        // Adicionar valor ao mapa de populações
                        if (country != null && year != null && value != null) {
                            populations
                                    .computeIfAbsent(country, k -> new TreeMap<>())
                                    .put(year, value);
                        }
                        return;
                    }
                    break;
            }
        }
    }
}