package dev.jsinco.avatarserver.server;

import dev.jsinco.avatarserver.configuration.Config;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class GenericServer {

    private final Server server;

    public GenericServer(Handler.Environment env) {
        Handler handler = new Handler(env);
        this.server = new Server();
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory());
        connector.setPort(Config.getInstance().getPort());
        connector.setHost(Config.getInstance().getHost());
        server.addConnector(connector);
        server.setHandler(handler);
    }

    public void start() {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
