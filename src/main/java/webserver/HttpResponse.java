package webserver;

import java.io.DataOutputStream;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    DataOutputStream dos;

    private HttpResponse(DataOutputStream dos) { this.dos = dos; }

    public static HttpResponse from(DataOutputStream dos) {
        return new HttpResponse(dos);
    }

    public void response200Header(int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302Header(String path, String cookie)  {
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

    public void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
