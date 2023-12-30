package org.example;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.DecimalFormat;

public class MainQuestionC {
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("src/main/resources/transformado.xml");

            String maxAbsoluteCountry = findMaxAbsolute(document);
            String maxRelativeCountry = findMaxRelative(document);

            double maxRelativeIncrease = calculateRelativeIncrease(document, maxRelativeCountry);
            long maxAbsoluteIncrease = calculateAbsoluteIncrease(document, maxAbsoluteCountry);

            Document resultDocument = createEmptyDocument();

            Element resultElement = resultDocument.createElement("result");

            Element relativeElement = resultDocument.createElement("relative");
            relativeElement.setAttribute("country", maxRelativeCountry);
            relativeElement.setAttribute("value", formatPercentage(maxRelativeIncrease));

            Element absoluteElement = resultDocument.createElement("absolute");
            absoluteElement.setAttribute("country", maxAbsoluteCountry);
            absoluteElement.setAttribute("value", formatNumber(maxAbsoluteIncrease));

            resultElement.appendChild(relativeElement);
            resultElement.appendChild(absoluteElement);

            resultDocument.appendChild(resultElement);

            String serializedDocument = serializeDocument(resultDocument);

            System.out.println(serializedDocument);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String findMaxAbsolute(Document document) {
        NodeList records = document.getElementsByTagName("record");
        String maxCountry = null;
        long maxAbsoluteIncrease = Long.MIN_VALUE;

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            String country = record.getAttribute("country");
            long absoluteIncrease = calculateAbsoluteIncrease(document, country);

            if (absoluteIncrease > maxAbsoluteIncrease) {
                maxAbsoluteIncrease = absoluteIncrease;
                maxCountry = country;
            }
        }

        return maxCountry;
    }

    private static String findMaxRelative(Document document) {
        NodeList records = document.getElementsByTagName("record");
        String maxCountry = null;
        double maxRelativeIncrease = Double.MIN_VALUE;

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            String country = record.getAttribute("country");
            double relativeIncrease = calculateRelativeIncrease(document, country);

            if (relativeIncrease > maxRelativeIncrease && !Double.isInfinite(relativeIncrease)) {
                maxRelativeIncrease = relativeIncrease;
                maxCountry = country;
            }
        }

        return maxCountry;
    }

    private static long calculateAbsoluteIncrease(Document document, String country) {
        NodeList records = document.getElementsByTagName("record");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            if (record.getAttribute("country").equals(country)) {
                NodeList values = record.getElementsByTagName("value");
                double firstYearValue = parseDouble(values.item(0).getTextContent());
                double lastYearValue = parseDouble(values.item(values.getLength() - 1).getTextContent());
                return Math.round(lastYearValue - firstYearValue);
            }
        }
        return 0;
    }

    private static double calculateRelativeIncrease(Document document, String country) {
        NodeList records = document.getElementsByTagName("record");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            if (record.getAttribute("country").equals(country)) {
                NodeList values = record.getElementsByTagName("value");
                double firstYearValue = parseDouble(values.item(0).getTextContent());
                double lastYearValue = parseDouble(values.item(values.getLength() - 1).getTextContent());
                if (firstYearValue != 0) {
                    return ((lastYearValue - firstYearValue) / firstYearValue) * 100.0;
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            }
        }
        return 0.0;
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static String formatPercentage(double value) {
        if (Double.isInfinite(value)) {
            return "Infinity%";
        } else {
            return new DecimalFormat("#,##0.00").format(value) + "%";
        }
    }

    private static String formatNumber(long value) {
        return new DecimalFormat("#,##0").format(value);
    }

    private static Document createEmptyDocument() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.newDocument();
    }

    private static String serializeDocument(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource domSource = new DOMSource(document);

            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);

            transformer.transform(domSource, streamResult);

            return writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}