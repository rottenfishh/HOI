package ru.nsu.kolodina.crawler;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class Crawler implements Runnable {
    final AtomicInteger counter;
    String url;
    ResponseFormat response;
    List<String> messages;

    @Override
    public void run() {
        Walker walker = new Walker();
        walker.getResponse(url, response);
        messages.add(response.getMessage());

        if (response.successors != null) {
            for (String i : response.successors) {
                String newUrl = "http://localhost:8080/" + i;
                ResponseFormat newResponseFormat = new ResponseFormat();
                counter.incrementAndGet();
                Thread t = Thread.ofVirtual().name("Walker Thread")
                        .start(new Crawler(counter, newUrl, newResponseFormat, messages));
            }
        }
        synchronized (counter) {
            int count = counter.decrementAndGet();
            if (count == 0) {
                counter.notifyAll();
            }
        }
//        for (Thread t : threads) {
//            try {
//                t.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }
}
