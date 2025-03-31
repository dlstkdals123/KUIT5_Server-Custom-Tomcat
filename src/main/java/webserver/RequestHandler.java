package webserver;

import db.MemoryUserRepository;
import http.util.*;
import http.util.constant.*;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest request = HttpRequestUtils.parseRequest(br);

            if (request.getMethod().equals(HttpMethod.GET.getMethod())) {
                responseGet(dos, request);
                return;
            }

            if (request.getMethod().equals(HttpMethod.POST.getMethod())) {
                responsePost(dos, request);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            if (contentType.isEmpty())
                contentType = "text/html";
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path, String cookie)  {
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

    private void responseGet(DataOutputStream dos, HttpRequest request) throws IOException {
        try {
            if (request.getUrl().equals(Query.USER_LIST.getQuery())) {
                responseUserList(dos, request);
                return;
            }

            if (request.getUrl().equals(Query.NO_QUERY.getQuery())) {
                responseInitialPage(dos, request);
                return;
            }

            String filePath = HttpRequestUtils.getFilePath(request.getUrl());
            byte[] body = Files.readAllBytes(Paths.get(filePath));

            response200Header(dos, body.length, HttpRequest.getContentType(filePath));
            responseBody(dos, body);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseUserList(DataOutputStream dos, HttpRequest request) {
        try {
            HashMap<String, String> cookieList = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(request.getCookie());
            if (cookieList != null
                    && cookieList.containsKey("logined")
                    && cookieList.get("logined").equals("true")) {

                String filePath = HttpRequestUtils.getFilePath(URL.USER_LIST.getUrl());
                byte[] body = Files.readAllBytes(Paths.get(filePath));

                response200Header(dos, body.length, HttpRequest.getContentType(filePath));
                responseBody(dos, body);
                return;
            }
            response302Header(dos, URL.LOGIN.getUrl(), null);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseInitialPage(DataOutputStream dos, HttpRequest request) throws IOException {
        try {
            String filePath = HttpRequestUtils.getFilePath(URL.INDEX.getUrl());
            byte[] body = Files.readAllBytes(Paths.get(filePath));

            response200Header(dos, body.length, HttpRequest.getContentType(filePath));
            responseBody(dos, body);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responsePost(DataOutputStream dos, HttpRequest request) throws IOException {
        try {
            HashMap<String, String> params = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(request.getBody());
            MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

            if (request.getUrl().equals(Query.SIGNUP.getQuery())) {
                responseSignup(dos, memoryUserRepository, params);
                return;
            }

            if (request.getUrl().equals(Query.LOGIN.getQuery())) {
                responseLogin(dos, memoryUserRepository, params);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseSignup(DataOutputStream dos, MemoryUserRepository memoryUserRepository, HashMap<String, String> params) {
        try {
            memoryUserRepository.addUser(new User(params.get(QueryStringKey.ID.getKey()), params.get(QueryStringKey.PASSWORD.getKey()), params.get(QueryStringKey.NAME.getKey()), params.get(QueryStringKey.EMAIL.getKey())));
            response302Header(dos, URL.INDEX.getUrl(), null);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseLogin(DataOutputStream dos, MemoryUserRepository memoryUserRepository, HashMap<String, String> params) {
        try {
            String userId = params.get(QueryStringKey.ID.getKey());
            String password = params.get(QueryStringKey.PASSWORD.getKey());
            if (userId == null || password == null) {
                response302Header(dos, URL.LOGIN_FAILED.getUrl(), null);
                return;
            }

            User user = memoryUserRepository.findUserById(userId);
            if (user == null || !password.equals(user.getPassword())) {
                response302Header(dos, URL.LOGIN_FAILED.getUrl(), null);
                return;
            }

            response302Header(dos, URL.INDEX.getUrl(), "logined=true; Path=/");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
