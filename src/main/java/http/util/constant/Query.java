package http.util.constant;

public enum Query {
    NO_QUERY("/"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList");

    private final String query;

    Query(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
