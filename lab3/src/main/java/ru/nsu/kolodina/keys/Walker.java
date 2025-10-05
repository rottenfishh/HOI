package ru.nsu.kolodina.keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.json.JSONObject;

@AllArgsConstructor
public class Walker implements Runnable {

    String urlString;
    ResponseFormat response;
//
//    {
//        "message": "some text",
//            "successors": ["path_1", "path_2", "path_3", ...,
//        "path_n"],
//    }
//
    public ResponseFormat parseJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        String message = jsonObject.getString("message");
        List<String> successors = jsonObject.getJSONArray("successors")
                .toList().stream().map(Object::toString).toList();

        ResponseFormat resp = new ResponseFormat();
        resp.setMessage(message);
        resp.setSuccessors(successors);
        return resp;
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
        System.out.println("walker" + content.toString());
        return content.toString();
    }

    public HttpURLConnection openConnection(){
        URI uri = URI.create(urlString);
        HttpURLConnection con = null;
        try {
            URL url = uri.toURL();
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

    @Override
    public void run() {
        HttpURLConnection connection = openConnection();

        int status = 0;
        try {
            status = connection.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (status != 200) {
            return;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String content = readInput(in);
        response = parseJson(content);
    }
}
