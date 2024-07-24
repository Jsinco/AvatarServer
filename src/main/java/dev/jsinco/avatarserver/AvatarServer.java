package dev.jsinco.avatarserver;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public final class AvatarServer extends JavaPlugin implements Listener {

    // TODO: Configuration, abstract stuff a bit more into different functions, etc.

    private Server server;

    @Override
    public void onLoad() {
        server = new Server();
    }

    @Override
    public void onEnable() {
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory());
        connector.setPort(26277);
        server.addConnector(connector);

        for (Player player : Bukkit.getOnlinePlayers()) {
            System.out.println(player.getPlayerProfile().getTextures().getSkin());
        }


        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                if (!request.getPathInfo().equals("/avatar")) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    baseRequest.setHandled(true);
                    return;
                }

                String uuidRaw = request.getParameter("uuid");
                String sizeRaw = request.getParameter("size");

                if (uuidRaw == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    baseRequest.setHandled(true);
                    return;
                }

                UUID uuid;
                try {
                    uuid = UUID.fromString(uuidRaw);
                } catch (IllegalArgumentException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    baseRequest.setHandled(true);
                    return;
                }

                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    baseRequest.setHandled(true);
                    return;
                }

                int size;
                if (sizeRaw == null) {
                    size = 8;
                } else {
                    try {
                        size = Integer.parseInt(sizeRaw);
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        baseRequest.setHandled(true);
                        return;
                    }
                }

                BufferedImage image;
                if (size == 8) {
                    image = ImageFactory.getAvatarImage(player.getPlayerProfile().getTextures().getSkin());
                } else {
                    image = ImageFactory.resizeUsingJavaAlgo(ImageFactory.getAvatarImage(player.getPlayerProfile().getTextures().getSkin()), size, size);
                }


                if (image == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    baseRequest.setHandled(true);
                    return;
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] imageData = baos.toByteArray();

                response.setContentType("image/png");
                response.getOutputStream().write(imageData);

                baseRequest.setHandled(true);
            }

        });


        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                server.start();
                server.join();
            } catch (InterruptedException e) {
                getLogger().log(Level.INFO, "Stopping Jetty");
            } catch (Exception e) {
                getLogger().log(Level.INFO, "Error starting Jetty", e);
            }
        });
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Stopping BunJetty");
        try {
            server.stop();
            getServer().getScheduler().cancelTasks(this); // Just cancel jetty this way
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}