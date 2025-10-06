package ru.nsu.kolodina.crawler;

import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<String> messages = Collections.synchronizedList(new ArrayList<>());
        ResponseFormat responseFormat = new ResponseFormat();
        String url = "http://localhost:8080";
        Thread t = Thread.ofVirtual().name("Walker Thread")
                .start(new Crawler(url, responseFormat, messages));
        t.join();
        for (String i : messages) {
            System.out.println(i);
        }
        System.out.println(messages.size());
    }
}