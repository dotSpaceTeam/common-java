package dev.dotspace.common.code.event;

import dev.dotspace.common.event.SimpleEventManager;

import java.util.concurrent.CompletableFuture;

public class MainEvent {

  public static void main(String[] args) {
    final SimpleEventManager eventManager = new SimpleEventManager();

    eventManager.registerEvent(TimeEvent.class, testEvent -> {


    });


    eventManager.registerEvent(TestEvent.class, 100, testEvent -> {

    });

  }

}
