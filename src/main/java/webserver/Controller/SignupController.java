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

public class SignupController implements Controller {
    private static final Logger log = Logger.getLogger(SignupController.class.getName());
    private static final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();


    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            HashMap<String, String> params = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(request.getBody());

            memoryUserRepository.addUser(new User(params.get(QueryStringKey.ID.getKey()), params.get(QueryStringKey.PASSWORD.getKey()), params.get(QueryStringKey.NAME.getKey()), params.get(QueryStringKey.EMAIL.getKey())));

            response.response302Header(URL.INDEX.getUrl(), null);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
