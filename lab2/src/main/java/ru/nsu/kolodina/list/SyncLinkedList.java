package ru.nsu.kolodina.list;

import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

@Getter
@Setter
public class SyncLinkedList<T> implements Iterable<T> {

    Node<T> head = new Node<T>();

    public void add(T data) {
        Node<T> node = new Node<>();
        node.setData(data);
        node.setNext(null);

        head.lock.lock();

        node.next = head.next;
        head.next = node;

        head.lock.unlock();
    }

    @Override
    public Iterator<T> iterator()
    {
        return new ListIterator<>(this);
    }

    public class ListIterator<T> implements Iterator<T>{
        Node<T> current;

        public ListIterator(SyncLinkedList<T> list) {
            current = list.getHead();

            Node<T> temp = current;
            current.lock.lock();
            current = current.next;

            temp.lock.unlock();
        }

        public boolean hasNext() {
            return current != null;
        }

        public T next() {
            Node<T> prev = current;

            current.lock.lock();
            T data = current.getData();
            current = current.getNext();

            prev.lock.unlock();

            return data;
        }
    }
}
