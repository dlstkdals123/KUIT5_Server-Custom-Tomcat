package webserver;

import http.util.constant.Query;
import webserver.Controller.*;

import java.util.HashMap;

public class RequestMapper {
    private HttpRequest request;
    private HttpResponse response;
    private HashMap<String, Controller> controllers = new HashMap<>();

    RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        initMapper();
    }

    private void initMapper() {
        controllers.put(Query.NO_QUERY.getQuery(), new HomeController());
        controllers.put(Query.SIGNUP.getQuery(), new SignupController());
        controllers.put(Query.LOGIN.getQuery(), new LoginController());
        controllers.put(Query.USER_LIST.getQuery(), new ListController());
    }

    public void proceed() {
        if (controllers.containsKey(request.getUrl())) {
            controllers.get(request.getUrl()).execute(request, response);
            return;
        }

        Controller controller = new ForwardController();
        controller.execute(request, response);
    }

}
