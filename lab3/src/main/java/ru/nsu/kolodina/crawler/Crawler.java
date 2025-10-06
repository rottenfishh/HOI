package ru.nsu.kolodina.crawler;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Crawler implements Runnable {
    String url;
    ResponseFormat response;
    List<String> messages;

    @Override
    public void run() {
        Walker walker = new Walker();
        walker.getResponse(url, response);
        messages.add(response.getMessage());

        List<Thread> threads = new ArrayList<>();
        for (String i : response.successors) {
            String newUrl = "http://localhost:8080/" + i;
            ResponseFormat newResponseFormat = new ResponseFormat();
            Thread t = Thread.ofVirtual().name("Walker Thread")
                    .start(new Crawler(newUrl, newResponseFormat, messages));
            threads.add(t);
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
