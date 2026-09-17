package utils;

import exceptions.FrameworkException;
import model.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public final class CsvReader {

    private CsvReader() {
    }


    public static List<Customer> readCustomers(String classpathResource) {
        List<Customer> customers = new ArrayList<>();
        try (InputStream in = CsvReader.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new FrameworkException("csv file not found on classpath: " + classpathResource);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line = reader.readLine(); // header, skipped
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] fields = line.split(",", -1);
                    if (fields.length < 3) {
                        throw new FrameworkException("malformed csv row in " + classpathResource + ": " + line);
                    }
                    customers.add(new Customer(fields[0].trim(), fields[1].trim(), fields[2].trim()));
                }
            }
        } catch (IOException e) {
            throw new FrameworkException("could not read csv file: " + classpathResource, e);
        }
        return customers;
    }
}
