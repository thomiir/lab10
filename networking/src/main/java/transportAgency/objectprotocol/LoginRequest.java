package transportAgency.objectprotocol;

import transportAgency.dto.EmployeeDTO;

import java.io.Serial;

public record LoginRequest(EmployeeDTO employee) implements Request {
    @Serial
    private static final long serialVersionUID = 10L;
}
