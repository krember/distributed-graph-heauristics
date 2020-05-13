package com.graphs;

import java.util.AbstractQueue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Reader {

    private static final Logger logger = Logger.getLogger(Thread.currentThread().getName());
    private final Set<Vertex> neighbors;
    private final LinkedBlockingQueue<Message> messages;// = new LinkedBlockingQueue<>();

    public Reader(Set<Vertex> neighbors, LinkedBlockingQueue<Message> messages) {
        this.neighbors = neighbors;
        this.messages= messages;

        while (true) {
//            Message msg = null;
            try {
                Message msg = this.messages.take();  //blocks until message is available
//                logger.info("Message sent -> Content: " + msg.getContent() + ", Source: " + msg.getSenderId());
                if ("inactive".equals(msg.getContent())) {
                    this.neighbors.stream().filter(it -> it.getId() == msg.getSenderId()).collect(Collectors.toList()).get(0).setInactive();
                }
            } catch (InterruptedException ignored) {

            }
        }

    }

}
