package ru.nsu.kolodina.keys;

import com.sun.net.httpserver.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URI;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
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