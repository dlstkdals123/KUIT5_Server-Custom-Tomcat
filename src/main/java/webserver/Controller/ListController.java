package webserver.Controller;

import http.util.HttpRequestUtils;
import http.util.constant.URL;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListController implements Controller {
    private static final Logger log = Logger.getLogger(ListController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            HashMap<String, String> cookieList = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(request.getCookie());
            if (cookieList != null
                    && cookieList.containsKey("logined")
                    && cookieList.get("logined").equals("true")) {
                String filePath = URL.USER_LIST.getFilePath();
                byte[] body = Files.readAllBytes(Paths.get(filePath));

                response.response200Header(body.length, HttpRequest.getContentType(filePath));
                response.responseBody(body);
                return;
            }
            response.response302Header(URL.LOGIN.getUrl(), null);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
