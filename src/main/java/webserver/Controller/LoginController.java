package webserver.Controller;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.constant.QueryStringKey;
import http.util.constant.URL;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Controller {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());
    private static final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            HashMap<String, String> params = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(request.getBody());

            String userId = params.get(QueryStringKey.ID.getKey());
            String password = params.get(QueryStringKey.PASSWORD.getKey());

            if (userId == null || password == null) {
                response.response302Header(URL.LOGIN_FAILED.getUrl(), null);
                return;
            }

            User user = memoryUserRepository.findUserById(userId);
            if (user == null || !password.equals(user.getPassword())) {
                response.response302Header(URL.LOGIN_FAILED.getUrl(), null);
                return;
            }

            response.response302Header(URL.INDEX.getUrl(), "logined=true; Path=/");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
