package webserver;

import http.util.HttpMessage;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public static HttpMessage createHttpMessage(BufferedReader br) {
        try {
            String[] startLine = getStartLine(br);
            HashMap<String, String> header = (HashMap<String, String>) parseHeader(br);
            String queryString = "";
            if (header.containsKey("Content-Length")) {
                int requestContentLength = Integer.parseInt(header.get("Content-Length"));
                queryString = IOUtils.readData(br, requestContentLength);
            }

            return new HttpMessage(startLine, header, queryString);
        } catch (Exception e) {
            return new HttpMessage(new String[0], new HashMap<>(), "");
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
}
