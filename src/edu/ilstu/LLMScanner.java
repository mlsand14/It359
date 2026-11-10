package edu.ilstu;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class LLMScanner {

    //api configuration constants
    private static final String API_URL = "http://sushi.it.ilstu.edu:8080/api/v1/chat/completions";
    private static final String API_KEY = "sk-cc77247348044cbc96a3a0ea3d3e7682"; 
    private static final String MODEL_NAME = "llama3.1:latest";

    //method to send email details to llama 3.1  ai model and returns the ai's decision and reasoning
    public static String scanEmail(String sender, String subject, String body) {
        try {
        
            //combines input into a single string to get rid of any unneccessary characters or line breaks to keep proper formatting
            String safeText = "Sender: " + clean(sender) + " | Subject: " + clean(subject) + " | Body: " + clean(body);

            String instructions = "You are a cybersecurity expert. Is this email phishing? Begin your response with 'VERDICT: PHISHING' or 'VERDICT: LEGITIMATE', then explain your reasoning in detail.";
        
            //contructing an openai format json payload
            /*model to specify llama 3.1, messages to define the instructions given to ai and the email data from user
            stream to ensure that the ai response is sent in one piece
            temperature for the ai to user analytical structure and natural reasoning */
            String json = "{"
                + "\"model\":\"" + MODEL_NAME + "\","
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":\"" + clean(instructions) + "\"},"
                + "{\"role\":\"user\",\"content\":\"" + safeText + "\"}"
                + "],"
                + "\"stream\":false," 
                + "\"temperature\":0.3"
                + "}";

            //intilizes httpclient so that it is strictly set to http/1.1
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1) 
                    .build();

            //contructs the http post request headers and attaches the encoded json body
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            //executes the http request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            //http status code 200 means it accepted the request and ai will generate a response
            if (response.statusCode() == 200) {
                return parseAiResponse(response.body());
            //if the http request did not work it will return the server error code
            } else {
                return "Server Rejected Request (Code " + response.statusCode() + ")\n" +
                       "Details: " + response.body();
            }

            //catches errors like if not connected to isu network
        } catch (Exception e) {
            return "Connection Error: " + e.getMessage();
        }
    }

    //method to get rid of characters that break json syntax
    private static String clean(String text) {
        if (text == null) return "";
    
        return text.replaceAll("[\"\\\\\n\r\t]", " ").trim();
    }

    //method to extract only the ai's response and gets rid of any unnecessary metadata details
    private static String parseAiResponse(String json) {
    try {
        //locates the start of the content
        String marker = "\"content\":\"";
        int start = json.indexOf(marker);
        if (start == -1) return "Raw Response: " + json;
        
        start += marker.length();

        //located the end of the text before json metadata flags
        int end = json.indexOf("\"}}", start);
        
        if (end == -1) {
            end = json.indexOf("\"},", start);
        }
        
        //find the end of the ai text and removes any metadata from the end
        if (end == -1) {
            int objectKey = json.indexOf(",\"object\"");
            if (objectKey != -1) {
                end = json.lastIndexOf("\"", objectKey);
            }
        }

        //validation for if the start and end indexes fail
       if (end == -1 || end <= start) {
            return "Error parsing text";
        }

        //extracts ai response text
        String result = json.substring(start, end);

        //convert escaped json control chracters into stardard text for terminal display purposes
        return result.replace("\\n", "\n")    
                     .replace("\\\"", "\"")   
                     .replace("\\\\", "\\"); 

    } catch (Exception e) {
        return "Parsing Error: " + e.getMessage();
    }
}
}
