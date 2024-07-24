package dev.jsinco.avatarserver;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class StandaloneServer {

    private static final Server server = new Server(8912);

    public static void main(String[] args) {
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                if (!request.getPathInfo().equals("/avatar")) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    baseRequest.setHandled(true);
                    return;
                }

                String texture = request.getParameter("texture");
                String sizeRaw = request.getParameter("size");

                if (texture == null) {
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
                    image = ImageFactory.getAvatarImage(texture);
                } else {
                    image = ImageFactory.resizeUsingJavaAlgo(ImageFactory.getAvatarImage(texture), size, size);
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

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
