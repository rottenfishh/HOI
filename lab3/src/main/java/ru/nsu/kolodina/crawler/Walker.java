package ru.nsu.kolodina.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import lombok.AllArgsConstructor;
import org.json.JSONObject;

@AllArgsConstructor
public class Walker{

    public HttpURLConnection openConnection(String urlString){
        URI uri = URI.create(urlString);
        HttpURLConnection con = null;
        try {
            URL url = uri.toURL();
            //System.out.println("Connecting to " + url);
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        return con;
    }

    public String readInput(BufferedReader in) {
        String inputLine;
        StringBuilder content = new StringBuilder();
        try {
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("walker" + content.toString());
        return content.toString();
    }

    public void parseJson(String json, ResponseFormat response) {
        JSONObject jsonObject = new JSONObject(json);
        String message = jsonObject.getString("message");
        List<String> successors = jsonObject.getJSONArray("successors")
                .toList().stream().map(Object::toString).toList();
        response.setMessage(message);
        response.setSuccessors(successors);
    }

    public void getResponse(String url, ResponseFormat response) {
        HttpURLConnection connection = openConnection(url);
        int status = 0;
        try {
            status = connection.getResponseCode();
        } catch (IOException e) {
            System.err.println("Status code error");
            return;
        }
        if (status != 200) {
            System.out.println("Connection error");
            return;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String content = readInput(in);
        parseJson(content, response);
    }
}
