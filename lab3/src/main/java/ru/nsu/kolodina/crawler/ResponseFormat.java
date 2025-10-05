package ru.nsu.kolodina.crawler;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResponseFormat {
    String message;
    List<String> successors;
}
