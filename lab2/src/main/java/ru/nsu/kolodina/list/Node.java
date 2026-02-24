package ru.nsu.kolodina.list;

import lombok.*;

import java.util.concurrent.locks.ReentrantLock;

@Setter
@Getter
public class Node<T> {
    T data = null;
    Node<T> next = null;
    ReentrantLock lock = new ReentrantLock();
}
