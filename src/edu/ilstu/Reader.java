package edu.ilstu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {

        //Variables
    String file; //temp variable to import email
    String sender; //sender of email
    String reciever; //recipent of email
    String content= ""; //main content of email
    String subject; //subject of the email
    String date; //date the email was sent
 //Scanner to scan email

//constructor, throws filenotfound exception if the eml file doesn't exit
    public Reader(String file) throws FileNotFoundException {
        File email = new File(file);
        Scanner scan = new Scanner(email);

        boolean reading= false;
        
        //while loop to read the file line by line
        while(scan.hasNextLine()){
            String line = scan.nextLine();
           
            if(line.isEmpty() && !reading){
                reading = true; //to indicate the body of the email is being read
                continue; //skips the blank line from the email header to body
            }
            if(reading){ //if statement to filter out any encoding headers so it doesn't confuse the ai
               if (line.startsWith("--") || line.startsWith("Content-Type:") || 
                    line.startsWith("Content-Transfer-Encoding:") || line.startsWith("MIME-Version:")) {
                    continue;
            }
            //removes html formatting tags
            String cleanLine = line.replaceAll("<[^>]*>", "").trim();

            //adds non-empty body lines to the content variable
            if(!cleanLine.isEmpty()){
                content += cleanLine + "\n";
            }
        }else{

        //if else statements to add the headers

            if(line.startsWith("From:")){
                sender = line.substring(5).trim();
            }
            else if(line.startsWith("To:")){
                reciever = line.substring(3).trim();
            }
            else if(line.startsWith("Subject:")){
                subject = line.substring(8).trim();
            }
            else if(line.startsWith("Date:")){
                date = line.substring(5).trim();
            }
        }
    }
    scan.close();
}


    //For testing purposes
    public String getSender(){ 
        return sender;
    }

    public String getReciever(){
        return reciever;
    }

    public String getContent(){
        return content;
    }

    public String getSubject(){
        return subject;
    }

    public String getDate(){
        return date;
    }

    }

