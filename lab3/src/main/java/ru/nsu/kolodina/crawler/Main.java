package ru.nsu.kolodina.crawler;

import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws ProtocolException {
        Spider spider = new Spider();
        List<String> messages = new ArrayList<>();
        String port = "8080";
        spider.runThrough(messages, "http://localhost:" + port);
    }
}