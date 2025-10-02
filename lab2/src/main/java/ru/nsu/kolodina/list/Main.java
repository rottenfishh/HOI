package ru.nsu.kolodina.list;


import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numOfThreads = Integer.parseInt(args[0]);
        SyncLinkedList<String> linkedList = new SyncLinkedList<>();
        Node<String> head = linkedList.head;

        String buffer = "";

        for (int i = 0; i < numOfThreads; i++) {
            Thread sortingThread = new Thread(new SortingThread(linkedList));
            sortingThread.start();
        }

        while (true) {
            if (scanner.hasNextLine()) {
                buffer = scanner.nextLine();
            } else {
                break;
            }
            if (!buffer.isEmpty())
                linkedList.add(String.copyValueOf(buffer.toCharArray()));
            else {
                for (String data : linkedList) {
                    System.out.println(data);
                }
            }

        }
    }
}