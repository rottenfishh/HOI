package ru.nsu.kolodina.list;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

@RequiredArgsConstructor
public class SortingArrayListThread implements Runnable{
    final List<String> list;
    final AtomicInteger counter;

    @Override
    public void run() {
        String temp;
        while(true) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized(list) {
                for (int i = 0; i < list.size() -1; i++) {
                    for (int j = i+1 ; j < list.size(); j++) {
                        if (list.get(i).compareTo(list.get(j)) > 0) {
                            temp = list.get(i);
                            list.set(i, list.get(j));
                            list.set(j, temp);
                            System.out.println(counter.incrementAndGet());
                        }
                    }
                }
            }
        }
    }
}
