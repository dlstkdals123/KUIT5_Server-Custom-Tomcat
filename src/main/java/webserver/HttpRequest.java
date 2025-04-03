package webserver;

import http.util.IOUtils;
import http.util.constant.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String[] startLineParam;
    private final HashMap<String, String> header;
    private final String body;

    private HttpRequest(String[] startLineParam, HashMap<String, String> header, String body) {
        this.startLineParam = startLineParam;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) {
        try {
            String[] startLine = getStartLine(br);
            HashMap<String, String> header = (HashMap<String, String>) parseHeader(br);
            String queryString = "";
            if (header.containsKey("Content-Length")) {
                int requestContentLength = Integer.parseInt(header.get("Content-Length"));
                queryString = IOUtils.readData(br, requestContentLength);
            }

            return new HttpRequest(startLine, header, queryString);
        } catch (Exception e) {
            return new HttpRequest(new String[0], new HashMap<>(), "");
        }
    }

    private static String[] getStartLine(BufferedReader br) {
        try {
            String[] startLine = br.readLine().split(" ");
            if (startLine.length != 3)
                throw new IOException("Invalid request");
            return startLine;
        } catch (Exception e) {
            return new String[0];
        }
    }

    private static Map<String, String> parseHeader(BufferedReader br) {
        try {
            HashMap<String, String> map = new HashMap<>();

            String line;
            while(!(line = br.readLine()).isEmpty()) {
                String[] keyValue = line.split(": ");
                map.put(keyValue[0], keyValue[1]);
            }

            return map;
        } catch (Exception e) {
            return new HashMap<>();
        }
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
