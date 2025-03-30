package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
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

            String[] tokens = br.readLine().split(" ");
            System.out.println(tokens[0] + " " + tokens[1] + " " + tokens[2]);
            if (tokens[1].equals("/"))
                tokens[1] = "/index.html";

            if (tokens[0].equals("GET") && tokens[1].endsWith(".html")) {
                String filePath = "webapp" + tokens[1];
                byte[] body = Files.readAllBytes(Paths.get(filePath));

                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            else if (tokens[0].equals("POST")) {
                String endpoint = tokens[1];
                String queryString = null;
                int requestContentLength = 0;

                while(true) {
                    final String line = br.readLine();
                    if (line.isEmpty())
                        break;
                    if (line.startsWith("Content-Length"))
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }

                queryString = IOUtils.readData(br, requestContentLength);

                if (endpoint.equals("/user/signup")) {
                    HashMap<String, String> params = (HashMap<String, String>) HttpRequestUtils.parseQueryParameter(queryString);
                    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
                    memoryUserRepository.addUser(new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email")));

                    String filePath = "webapp/index.html";
                    byte[] body = Files.readAllBytes(Paths.get(filePath));

                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
            }


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
