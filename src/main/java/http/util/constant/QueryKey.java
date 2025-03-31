package http.util.constant;

public enum QueryKey {
    ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String key;

    QueryKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
