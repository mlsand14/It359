package edu.ilstu;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;

public class Main {
    
    public static void main(String[] args) throws FileNotFoundException{
        //initilizes the JFileChoose that allows a user to pick a file from their system
        JFileChooser fileChooser = new JFileChooser();

        //opens the "file open" window
        int userSelection = fileChooser.showOpenDialog(null);

        //if statement to check if the user picked a valid file
        //if the file is valid, the Reader will read the file and print the details of the email
        if(userSelection == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
        Reader email = new Reader(selectedFile.getAbsolutePath());

        System.out.println("Sender:" + email.getSender());
        System.out.println("Receiver:" + email.getReciever());
        System.out.println("Subject:" + email.getSubject());
        System.out.println("Date:" + email.getDate());
        System.out.println("Content: " + email.getContent());

        //sends the clean email to the open webui server for phishing detection
        String aiResponse = LLMScanner.scanEmail(
                email.getSender(),
                email.getSubject(),
                email.getContent()
            );

            //prints the ai's response
         System.out.println("\n================ AI PHISHING ANALYSIS ================");
            System.out.println(aiResponse);
            System.out.println("=======================================================");

            //if the user did not select a file
        }else{
            System.out.println("No file was selected.");
        }

        


    }
}
