package webserver.Controller;

import http.util.constant.URL;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController implements Controller {
    private static final Logger log = Logger.getLogger(HomeController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            String filePath = URL.INDEX.getFilePath();
            byte[] body = Files.readAllBytes(Paths.get(filePath));

            response.response200Header(body.length, HttpRequest.getContentType(filePath));
            response.responseBody(body);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
