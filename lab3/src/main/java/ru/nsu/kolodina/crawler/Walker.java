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

    public HttpURLConnection openConnection(String urlString) throws IOException {
        URI uri = URI.create(urlString);
        URL url = uri.toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        return con;
    }

    public String readInput(BufferedReader in) throws IOException {
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
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
        HttpURLConnection connection = null;
        int status = 0;
        try {
            connection = openConnection(url);
            status = connection.getResponseCode();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (status != 200) {
            System.out.println("Connection error");
            return;
        }

        String content = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            content = readInput(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parseJson(content, response);
    }
}
