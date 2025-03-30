package http.util;

public enum URL {
    INDEX("/", "/index.html", "webapp/index.html"),
    USER_LIST("/user/userList", "/user/login.html", "webapp/user/list.html"),
    SIGNUP("/user/signup", INDEX.getRedirectPath(), INDEX.getFilePath()),
    LOGIN("/user/login", INDEX.getRedirectPath(), INDEX.getFilePath()),
    LOGIN_FAILED("/user/login", "/user/login_failed.html", "webapp/user/login_failed.html");

    private final String url;
    private final String redirectPath;
    private final String filePath;

    URL(String url, String redirectPath, String filePath) {
        this.url = url;
        this.redirectPath = redirectPath;
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public static URL fromURL(String url) {
        for (URL urlElement : URL.values()) {
            if (urlElement.getUrl().equals(url)) {
                return urlElement;
            }
        }
        return INDEX;
    }
}
