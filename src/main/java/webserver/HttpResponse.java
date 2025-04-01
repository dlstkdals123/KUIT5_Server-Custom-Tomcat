package webserver;

import db.MemoryUserRepository;
import http.util.HttpMessage;
import http.util.HttpRequestUtils;
import http.util.constant.Query;
import http.util.constant.QueryStringKey;
import http.util.constant.URL;
import model.User;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    DataOutputStream dos;

    private HttpResponse(DataOutputStream dos) { this.dos = dos; }

    public static HttpResponse from(DataOutputStream dos) {
        return new HttpResponse(dos);
    }

    public void forward(String filePath) {
        try {
            byte[] body = Files.readAllBytes(Paths.get(filePath));

            response200Header(body.length, HttpMessage.getContentType(filePath));
            responseBody(body);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(String path, String cookie) {
        response302Header(path, cookie);
    }

    private void response200Header(int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(String path, String cookie)  {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            if (cookie != null)
                dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void responseGet(HttpMessage request){
        if (request.getUrl().equals(Query.USER_LIST.getQuery())) {
            responseUserList(request);
            return;
        }

        if (request.getUrl().equals(Query.NO_QUERY.getQuery())) {
            responseInitialPage();
            return;
        }

        forward(HttpRequestUtils.getFilePath(request.getUrl()));
    }

    private void responseInitialPage() {
        forward(HttpRequestUtils.getFilePath(URL.INDEX.getUrl()));
    }

    private void responseUserList(HttpMessage request) {
        HashMap<String, String> cookieList = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(request.getCookie());
        if (cookieList != null
                && cookieList.containsKey("logined")
                && cookieList.get("logined").equals("true")) {

            forward(HttpRequestUtils.getFilePath(URL.USER_LIST.getUrl()));
            return;
        }
        redirect(URL.LOGIN.getUrl(), null);
    }

    public void responsePost(HttpMessage request) {
        HashMap<String, String> params = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(request.getBody());
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

        if (request.getUrl().equals(Query.SIGNUP.getQuery())) {
            responseSignup(memoryUserRepository, params);
            return;
        }

        if (request.getUrl().equals(Query.LOGIN.getQuery())) {
            responseLogin(memoryUserRepository, params);
        }
    }

    private void responseSignup(MemoryUserRepository memoryUserRepository, HashMap<String, String> params) {
        memoryUserRepository.addUser(new User(params.get(QueryStringKey.ID.getKey()), params.get(QueryStringKey.PASSWORD.getKey()), params.get(QueryStringKey.NAME.getKey()), params.get(QueryStringKey.EMAIL.getKey())));
        redirect(URL.INDEX.getUrl(), null);
    }

    private void responseLogin(MemoryUserRepository memoryUserRepository, HashMap<String, String> params) {
        String userId = params.get(QueryStringKey.ID.getKey());
        String password = params.get(QueryStringKey.PASSWORD.getKey());
        if (userId == null || password == null) {
            redirect(URL.LOGIN_FAILED.getUrl(), null);
            return;
        }

        User user = memoryUserRepository.findUserById(userId);
        if (user == null || !password.equals(user.getPassword())) {
            redirect(URL.LOGIN_FAILED.getUrl(), null);
            return;
        }

        redirect(URL.INDEX.getUrl(), "logined=true; Path=/");
    }
}
