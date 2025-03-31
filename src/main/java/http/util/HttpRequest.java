package http.util;

import http.util.constant.HttpHeader;

import java.util.HashMap;

public class HttpRequest {
    private String[] startLineParam;
    private HashMap<String, String> header;
    private String body;

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
}
