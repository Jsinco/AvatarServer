package dev.jsinco.avatarserver;

import dev.jsinco.avatarserver.server.GenericServer;
import dev.jsinco.avatarserver.server.Handler;

public final class StandaloneServer {


    public static void main(String[] args) {
        System.out.println("Starting Jetty");
        GenericServer genericServer = new GenericServer(Handler.Environment.STANDALONE);
        genericServer.start();
    }
}
