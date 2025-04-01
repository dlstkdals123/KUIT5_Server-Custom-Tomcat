package http.util.constant;

public enum QueryStringKey {
    ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String key;

    QueryStringKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
