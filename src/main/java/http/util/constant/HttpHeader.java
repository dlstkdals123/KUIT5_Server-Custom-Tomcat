package http.util.constant;

public enum HttpHeader {
    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length");

    private final String header;

    HttpHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
