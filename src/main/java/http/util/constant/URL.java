package http.util.constant;

public enum URL {
    INDEX("/index.html"),
    USER_LIST("/user/list.html"),
    LOGIN("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html");

    private final String url;

    URL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}