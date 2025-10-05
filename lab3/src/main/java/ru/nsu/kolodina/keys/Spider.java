package ru.nsu.kolodina.keys;

import com.sun.net.httpserver.Request;

import java.util.ArrayList;
import java.util.List;

public class Spider {

    public static Thread initThread(String url, ResponseFormat response) {
        Thread.Builder builder = Thread.ofVirtual().name("Walker Thread");
        return builder.start(new Walker(url, response));
    }

    public String runThrough(List<String> messages, String url) {
        ResponseFormat responseFormat = new ResponseFormat();
        Thread t = initThread(url, responseFormat);
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (responseFormat.successors == null) {
            return responseFormat.message;
        }
        for (String i : responseFormat.successors) {
            messages.add(runThrough(messages, "http://localhost:"+ url));
        }
        System.out.println("spider " + responseFormat.message);
        return responseFormat.message;
    }
}
