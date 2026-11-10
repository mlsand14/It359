package edu.ilstu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Reader {


    String file; //temp variable to import email
    String sender; //sender of email
    String reciever; //recipent of email
    String content= ""; //main content of email
    String subject; //subject of the email
    String date; //date the email was sent
 
    
    //phishing detection variables
    String replyTo = "";
    String spf = "";
    String dkim = "";
    List<String> urls = new ArrayList<>();

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

            //for loop to extract urls
            //line.split to split a line of text into individual words, word.startsWith to identify if its the start of a link
            //word.replaceAll to get rid of html tags
            //urls.add to add a url to the urls list
            for(String word : line.split("\\s+")){
                if(word.startsWith("http://") || word.startsWith("https://")){
                    urls.add(word.replaceAll("<[^>]*>", ""));
                }
            }
        }else{

        //if else statements to add the headers
            String lower = line.toLowerCase();

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

            //extracts the reply to address
            if (lower.startsWith("reply-to:")) {
                    replyTo = line.substring(9).trim().replaceAll("[<>]", "");
                }

            //identifies if the server is authorized to send the email    
            if (lower.contains("spf=")) {
                spf = extractStatus(line, "spf=");
            }

            //identifies if the email is changed or not
            if (lower.contains("dkim=")) {
                dkim = extractStatus(line, "dkim=");
            }
    }
}
    scan.close();
}

//method that searches the email header line for a spf or dkim tag and returns pass or fail to Reader file
private String extractStatus(String line, String key) {
        int index = line.toLowerCase().indexOf(key);
        return line.substring(index + key.length()).split("[;\\s]")[0].trim();
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

    //getters for headers and urls
    public String getReplyTo(){ 
        return replyTo; 
    }
    public String getSpf(){ 
        return spf; 
    }
    public String getDkim(){ 
        return dkim; 
    }
    public List<String> getUrls(){ 
        return urls;
    }

    }

