package http.util;

import http.util.constant.HttpHeader;

import java.util.HashMap;

public class HttpRequest {
    private final String[] startLineParam;
    private final HashMap<String, String> header;
    private final String body;

    public HttpRequest(String[] startLineParam, HashMap<String, String> header, String body) {
        this.startLineParam = startLineParam;
        this.header = header;
        this.body = body;
    }

    public String getMethod() {
        return startLineParam[0];
    }

    public String getUrl() {
        return startLineParam[1];
    }

    public String getCookie() {
        return header.get(HttpHeader.COOKIE.getHeader());
    }

    public String getBody() {
        return body;
    }

    public static String getContentType(String filePath) {
        int index = filePath.lastIndexOf(".");
        String extension = filePath.substring(index + 1);

        if (extension.equals("html") || extension.equals("css") || extension.equals("js")) {
            return "text/" + extension;
        }

        if (extension.equals("jpeg") || extension.equals("png")) {
            return "image/" + extension;
        }

        return "";
    }
}
