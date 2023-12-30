package org.example;


import org.example.elements.Population;
import org.example.elements.Record;
import org.example.elements.Value;

import javax.xml.bind.*;
import java.io.File;

public class MainQuestionB {
    public static void main(String[] args) {
        try {
            JAXBContext context = JAXBContext.newInstance(Population.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Population population = (Population) unmarshaller.unmarshal(new File("src/main/resources/transformado.xml"));

            for (Record record : population.getRecords()) {
                System.out.println("Country: " + record.getCountry());
                for (Value value : record.getValues()) {
                    System.out.println("  Year: " + value.getYear() + ", Value: " + value.getValue());
                }
            }

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(population, new File("src/main/resources/transformado-marshalled.xml"));

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}