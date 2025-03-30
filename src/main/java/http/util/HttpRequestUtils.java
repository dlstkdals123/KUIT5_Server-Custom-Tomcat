package http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    private static final String rootPath = "webapp";

    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static HttpRequest parseRequest(BufferedReader br) throws IOException {
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

    private static String[] getStartLine(BufferedReader br) throws IOException {
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

    public static String getFilePath(String url) {
        int index = url.lastIndexOf(".");
        if (index != -1) {
            return rootPath + url;
        }
        return URL.fromURL(url).getFilePath();
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
