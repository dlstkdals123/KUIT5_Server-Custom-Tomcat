package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import http.util.URL;
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

            HashMap<String, String> header = (HashMap<String, String>) HttpRequestUtils.parseHeader(br);

            if (header.get("method").equals("GET")) {
                String filePath = HttpRequestUtils.getFilePath(header.get("url"));

                System.out.println("Cookie :" + header.get("Cookie"));

                if (header.get("url").equals(URL.USER_LIST.getUrl())) {
                    HashMap<String, String> cookieList = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(header.get("Cookie"));
                    if (!cookieList.get("logined").equals("true")) {
                        filePath = URL.INDEX.getFilePath();
                    }
                }

                byte[] body = Files.readAllBytes(Paths.get(filePath));

                response200Header(dos, body.length, header.get("Accept"));
                responseBody(dos, body);

                return;
            }

            if (header.get("method").equals("POST")) {
                int requestContentLength = Integer.parseInt(header.get("Content-Length"));

                String queryString = IOUtils.readData(br, requestContentLength);
                HashMap<String, String> params = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(queryString);
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

                if (header.get("url").equals(URL.SIGNUP.getUrl())) {
                    memoryUserRepository.addUser(new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email")));
                    response302Header(dos, URL.SIGNUP.getRedirectPath(), null);
                    return;
                }

                if (header.get("url").equals(URL.LOGIN.getUrl())) {
                    User user = memoryUserRepository.findUserById(params.get("userId"));
                    if (user == null
                        || !params.get("password").equals(user.getPassword())) {
                        response302Header(dos, URL.LOGIN_FAILED.getRedirectPath(), null);
                        return;
                    }
                    String cookie = "logined=true; Path=/";
                    response302Header(dos, URL.LOGIN.getRedirectPath(), cookie);
                }
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

    private void response302Header(DataOutputStream dos, String path, String cookie) {
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
