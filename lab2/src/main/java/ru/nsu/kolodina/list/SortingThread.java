package ru.nsu.kolodina.list;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


import static java.lang.Thread.sleep;

@RequiredArgsConstructor
public class SortingThread implements Runnable{
    @NonNull
    SyncLinkedList<String> list;
    Node<String> prev, cur0, cur1;

    public boolean lockThreeElems() {
        prev.lock.lock();
        cur0 = prev.next;
        if (cur0 == null) {
            prev.lock.unlock();
            return false;
        }

        cur0.lock.lock();
        cur1 = cur0.next;
        if (cur1 == null) {
            cur0.lock.unlock();
            prev.lock.unlock();
            return false;
        }

        cur1.lock.lock();
        return true;
    }

    public void swapNodes(Node<String> prev, Node<String> cur0,
                          Node<String> cur1) {
        cur0.next = cur1.next;
        prev.next = cur1;
        cur1.next = cur0;
    }

    @Override
    public void run() {
        boolean change = false;
        boolean flag = false;

        while(true) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            change = true;

            while(change) {
                prev = list.getHead();
                change = false;

                while(lockThreeElems()) {

                    if (cur0.getData().compareTo(cur1.getData()) > 0) {
                        change = true;
                        swapNodes(prev, cur0, cur1);
                    }
                    cur1.lock.unlock();
                    cur0.lock.unlock();
                    prev.lock.unlock();

                    if (change) {
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        prev = cur1;
                    } else {
                        prev = cur0;
                    }
                }
            }
        }
    }
}
