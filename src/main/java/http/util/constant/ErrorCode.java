package http.util.constant;

public enum ErrorCode {
    INVALID_URL(404, "GET_01", "존재하지 않는 URL입니다."),
    INVALID_POST(404, "POST_01", "존재하지 않는 POST입니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
