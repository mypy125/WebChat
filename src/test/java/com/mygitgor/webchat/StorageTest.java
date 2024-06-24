package com.mygitgor.webchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StorageTest {
    private Storage storage;

    @BeforeEach
    public void setup() {
        storage = new Storage();
    }

    @Test
    public void testAddRecordJoined(){
        assertEquals(0, storage.size());

        storage.addRecordJoined("Test User");
        assertEquals(1, storage.size());

        Storage.ChatMessage message = storage.getMessages().peek();
        assertEquals("", message.getName());
        assertEquals("Test User", message.getMessage());
    }

    @Test
    public void testAddRecord(){
        assertEquals(0, storage.size());
        storage.addRecord("Test User", "Hello, world!");
        assertEquals(1, storage.size());

        Storage.ChatMessage message = storage.getMessages().peek();
        assertEquals("Test User", message.getName());
        assertEquals("Hello, world!", message.getMessage());
    }
}
