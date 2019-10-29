package main.data;

import main.model.account.Account;
import main.model.person.Employee;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.StringTokenizer;

import static main.utils.Utils.DATA_FOLDER;

public class EmployeeRepository {
    private static EmployeeRepository instance = null;
    private ArrayList<Employee> employees;

    private static final String passKey = "CMSLOGIN";
    private static SecretKeySpec secretKey;
    private static byte[] key;

    private EmployeeRepository() {
        employees = new ArrayList<>();
    }

    public ArrayList<Employee> getEmployees() { return employees; }

    public static EmployeeRepository getInstance() {
        if(instance == null) {
            synchronized (EmployeeRepository.class) {
                if(instance == null) {
                    instance = new EmployeeRepository();
                }
            }
        }

        return instance;
    }

    public void saveEmployees() {
        try {
            FileWriter fw = new FileWriter(DATA_FOLDER + File.separator + "employees.dat");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for(Employee e : employees) {
                pw.println(e.toFile());
            }

            pw.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadEmployees() {
        try {
            FileReader fr = new FileReader(DATA_FOLDER + File.separator + "employees.dat");
            BufferedReader br = new BufferedReader(fr);

            employees.clear();

            for(String data = br.readLine(); data != null; data = br.readLine()) {
                StringTokenizer token = new StringTokenizer(data, ";");
                String name = token.nextToken();
                String phone = token.nextToken();
                String rank = token.nextToken();
                double salary = Double.parseDouble(token.nextToken());
                String userID = token.nextToken();
                String password = token.nextToken();

                Employee emp = new Employee(name, phone, rank, salary);

                if(!userID.equalsIgnoreCase("null")) {
                    emp.setAccount(new Account(userID, password));
                }

                employees.add(emp);
            }

            br.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ENCRYPTION METHODS [AES]

    private void setKey(String myKey) throws NoSuchAlgorithmException {
        MessageDigest sha;
        key = myKey.getBytes(StandardCharsets.UTF_8);
        sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, "AES");
    }

    public String encrypt(String strToEncrypt)
    {
        try
        {
            setKey(passKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt)
    {
        try
        {
            setKey(passKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
