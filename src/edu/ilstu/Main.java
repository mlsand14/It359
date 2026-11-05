package edu.ilstu;
import java.io.FileNotFoundException;

public class Main {
    
    public static void main(String[] args) throws FileNotFoundException{
        Reader email = new Reader("Testemail.eml");

        System.out.println("Sender:" + email.getSender());
        System.out.println("Receiver:" + email.getReciever());
        System.out.println("Subject:" + email.getSubject());
        System.out.println("Date:" + email.getDate());
        System.out.println("Content: " + email.getContent().substring(4));
    }
}
