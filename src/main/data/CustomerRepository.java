package main.data;

import main.model.person.Address;
import main.model.person.Customer;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static main.utils.Utils.DATA_FOLDER;
import static main.utils.Utils.MALAYSIA;

public class CustomerRepository {
    private static CustomerRepository instance;
    private ArrayList<Customer> customers;

    private CustomerRepository() { customers = new ArrayList<>(); }

    public static CustomerRepository getInstance() {
        if(instance == null) {
            synchronized (CustomerRepository.class) {
                if(instance == null) {
                    instance = new CustomerRepository();
                }
            }
        }
        return instance;
    }

    public void saveCustomers() {
        try {
            PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(DATA_FOLDER + File.separator + "customers.dat"))
            );

            for(Customer c : customers)
                pw.println(c.toFile());

            pw.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCustomers() {
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(DATA_FOLDER + File.separator + "customers.dat")
            );

            customers.clear();

            for(String data = br.readLine(); data != null; data = br.readLine()) {
                StringTokenizer token = new StringTokenizer(data, ";");

                String name = token.nextToken();
                String phone = token.nextToken();
                String id = token.nextToken();
                String nationality = token.nextToken();
                String street1 = token.nextToken();
                String street2 = token.nextToken();
                String postCode = token.nextToken();
                String district = token.nextToken();
                String state = token.nextToken();
                double spent = Double.parseDouble(token.nextToken());


                Customer cust = new Customer(name, phone, id, nationality, null, spent);

                if(nationality.equalsIgnoreCase(MALAYSIA)) {
                    if(street2.equalsIgnoreCase("null"))
                        street2 = "";

                    if(district.equalsIgnoreCase("null"))
                        district = "";

                    cust.setAddress(new Address(street1, street2, postCode, district, state));
                }

                customers.add(cust);
            }

            br.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Customer> getCustomers() { return customers; }
}
