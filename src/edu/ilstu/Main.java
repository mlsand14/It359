package edu.ilstu;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;

public class Main {
    
    public static void main(String[] args) throws FileNotFoundException{
        //initilizes the JFileChoose that allows a user to pick a file from their system
        JFileChooser fileChooser = new JFileChooser();

        // Creates a window that welcomes the user to the email program
        int welcome = JOptionPane.showOptionDialog(
                null,
        "Welcome our IT359 project - an email scanner!\n\n"
                + "This program analyzes email files (.eml) "
                + "for potential phishing threats and returns an AI analysis of the email contents. \n\n"
                + "Click continue to begin scanning an email.",
        "Welcome",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        new String[]{"Continue"},
                "Continue"
        );

        //exits program if user clicks X button
        if (welcome != 0) {
            System.exit(0);
        }

        //prompt window; requests a .eml file from user
        JOptionPane.showMessageDialog(
                null,
                "Please enter a .eml file to be scanned.",
                "Email Scanner",
                JOptionPane.INFORMATION_MESSAGE
        );

        //while statement to check if the user picked a valid file and loop file request otherwise
        //if the file is valid, the Reader will read the file and print the details of the email
        File selectedFile = null;
        boolean validFile = false;

        while (!validFile) {
            //opens the "file selection" window
           int userSelection = fileChooser.showOpenDialog(null);

           //allows user to exit
            if (userSelection != JFileChooser.APPROVE_OPTION){
                break;
            }

            selectedFile = fileChooser.getSelectedFile();

            //checks if file ends in .eml
            if (selectedFile.getName().toLowerCase().endsWith(".eml")) { validFile = true;
             } else {
                 JOptionPane.showMessageDialog(
                     null,
                      "Invalid file type. Please enter a .eml file.",
                     "Invalid File Type",
                     JOptionPane.ERROR_MESSAGE
                 );
             }

        }   if(validFile) {
            selectedFile = fileChooser.getSelectedFile();

        Reader email = new Reader(selectedFile.getAbsolutePath());

        //prints email details
        System.out.println("--Parsed Email Details--");
        System.out.println("Sender:" + email.getSender());
        System.out.println("Receiver:" + email.getReciever());
        System.out.println("Subject:" + email.getSubject());
        System.out.println("Date:" + email.getDate());
        System.out.println("Content: " + email.getContent());

        //initiates the flag variable 
        int flags =0;
        System.out.println("\n--Technical Red Flags--");

        //check for spf flag(sender policy framework)
        //if it says fail it means the email was sent from an unauthorized server
        if(email.getSpf().equalsIgnoreCase("fail")){
            System.out.println("SPF Authentication Failed");
            flags++;
        }

        //checks for dkim flag(domainkeys identified mail)
        //if it says it failed then the content was changed after being sent
        if(email.getDkim().equalsIgnoreCase("fail")){
            System.out.println("DKIM Signature Failed");
            flags++;
        }
        
        //checks if a reply to address exists and checks if it matches the sender domain 
        if(!email.getReplyTo().isEmpty() &&!email.getSender().toLowerCase().contains(email.getReplyTo().toLowerCase())){
            System.out.println("Reply To Mismatch: " + email.getReplyTo());
            flags++;
        }

        //checks for suspicious urls that uses an ip rathern than a domain name
        for (String url : email.getUrls()) {
                if (url.matches("https?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*")) {
                    System.out.println("Suspicious IP-Based URL: " + url);
                    flags++;
                }
            }
    
        //check is there are no flags
        if (flags == 0) {
            System.out.println("No technical red flags found.");
        }

        //sends the clean email to the open webui server for phishing detection
        String aiResponse = LLMScanner.scanEmail(
                email.getSender(), email.getSubject(),email.getContent());
        
        //if the ai detects the email as phishing, the email gets marked as phishing
        boolean isPhishing = aiResponse.contains("VERDICT: PHISHING");

        String securityLevel;
        String reason;

            //if else statements for risk ratings
            
            if (isPhishing && flags > 0) {
                securityLevel = "Critical High Risk"; reason= "Reason: Both AI analysis and technical headers flagged threats.";
            } else if (isPhishing) {
                securityLevel = "High Risk"; reason= "Reason: AI detected phishing / social engineering tactics.";
            } else if (flags > 0) {
                securityLevel = "Medium Risk"; reason= "Reason: Technical header or link anomalies were detected.";
            } else {
                securityLevel = "Safe"; reason= "No technical anomalies or phishing language found.";
            }

            //prints the ai's response
         System.out.println("\n================ AI PHISHING ANALYSIS ================");
         System.out.println("Security Level: " + securityLevel);   
         System.out.println(aiResponse);
            System.out.println("=======================================================");

            //if the user did not select a file
        }else{
            System.out.println("No file was selected.");
        }

        


    }
}
