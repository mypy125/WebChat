package com.mygitgor.webchat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Storage {

    @Getter
    private Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();

    public void addRecord(String user, String message) {
        messages.add(new ChatMessage(user, message));
    }

    @Getter
    @AllArgsConstructor
    public static class ChatMessage{
        private String name;
        private String message;
    }

}
