package ru.nsu.kolodina.crawler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<String> messages = Collections.synchronizedList(new ArrayList<>());
        ResponseFormat responseFormat = new ResponseFormat();
        String url = "http://localhost:8080";
        AtomicInteger counter = new AtomicInteger();
        Thread t = Thread.ofVirtual().name("Walker Thread")
                .start(new Crawler(counter, url, responseFormat, messages));
        synchronized (counter) {
            counter.incrementAndGet();
            counter.wait();
        }
        for (String i : messages) {
            System.out.println(i);
        }
        System.out.println(messages.size());
        messages.sort(String::compareTo);
    }
}
