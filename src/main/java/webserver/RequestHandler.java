package webserver;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest request = HttpRequest.from(br);
            HttpResponse response = HttpResponse.from(dos);

            RequestMapper requestMapper = new RequestMapper(request, response);
            requestMapper.proceed();
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}
