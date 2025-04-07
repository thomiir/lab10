package transportAgency.objectprotocol;

import java.io.Serial;

public class ErrorResponse implements Response{
    @Serial
    private static final long serialVersionUID = 5L;
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
