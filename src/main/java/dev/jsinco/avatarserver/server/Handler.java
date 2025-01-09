package dev.jsinco.avatarserver.server;

import dev.jsinco.avatarserver.AvatarServer;
import dev.jsinco.avatarserver.configuration.Config;
import dev.jsinco.avatarserver.image.ImageFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Handler extends AbstractHandler {

    private final Environment env;

    public Handler(Environment env) {
        this.env = env;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

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

        if (!Config.getInstance().useTextureHashes() && env == Environment.PAPER) {
            image = ImageFactory.getAvatarImage(AvatarServer.getTextureFromUUIDOrName(texture));
        } else {
            image = ImageFactory.getAvatarImage(texture);
        }

        if (size != 8) {
            image = ImageFactory.resizeUsingJavaAlgo(image, size, size);
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


    public enum Environment {
        STANDALONE, PAPER
    }
}

