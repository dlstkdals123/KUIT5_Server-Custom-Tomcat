package webserver.Controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public interface Controller {
    void execute(HttpRequest request, HttpResponse response);
}
