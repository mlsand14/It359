package edu.ilstu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {

        //Variables
    String file; //temp variable to import email
    String sender; //sender of email
    String reciever; //recipent of email
    String content; //main content of email
    String subject; //subject of the email
    String date; //date the email was sent
 //Scanner to scan email


    public Reader(String file) throws FileNotFoundException {
        File email = new File(file);
        Scanner scan = new Scanner(email);

        while(scan.hasNextLine()){
            String line = scan.nextLine();
            boolean reading = false;
            if(line.isEmpty()){
                reading = true;
                continue;
            }
            if(reading){
                content += line + "\n";
            }

            if(line.startsWith("From:")){
                sender = line.substring(5);
            }
            else if(line.startsWith("To:")){
                reciever = line.substring(3);
            }
            else if(line.startsWith("Subject:")){
                subject = line.substring(8);
            }
            else if(line.startsWith("Date:")){
                date = line.substring(5);
            }
            else{
                if(line.startsWith("Thread-Topic:")){
                    continue;
                }
                else if(line.startsWith("Thread-Index:")){
                    continue;
                }
                else if(line.startsWith("Date:")){
                    continue;
                }
                else{
                content += line + "\n";
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

