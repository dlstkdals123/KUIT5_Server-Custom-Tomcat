package http.util;

import java.io.BufferedReader;
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

    public static Map<String, String> parseHeader(BufferedReader br) {
        try {
            HashMap<String, String> map = startLine(br);

            String line;
            while(!(line = br.readLine()).isEmpty()) {
                String[] keyValue = line.split(": ");
                map.put(keyValue[0], keyValue[1]);
            }
            addAcceptHeader(map);

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

    private static void addAcceptHeader(HashMap<String, String> map) {
        String filePath = getFilePath(map.get("url"));

        int index = filePath.lastIndexOf(".");
        String extension = filePath.substring(index + 1);

        if (extension.equals("html") || extension.equals("css") || extension.equals("js")) {
            map.put("Accept", "text/" + extension);
            return;
        }

        if (extension.equals("jpeg") || extension.equals("png")) {
            map.put("Accept", "image/" + extension);
        }
    }

    private static HashMap<String, String> startLine(BufferedReader br) {
        try {
            String[] startLine = br.readLine().split(" ");
            HashMap<String, String> startLineMap = new HashMap<>();
            startLineMap.put("method", startLine[0]);
            startLineMap.put("url", startLine[1]);
            startLineMap.put("version", startLine[2]);

            return startLineMap;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
