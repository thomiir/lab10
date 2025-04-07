package transportAgency.objectprotocol;

import transportAgency.dto.EmployeeDTO;

import java.io.Serial;

public class LogoutRequest implements Request {
    @Serial
    private static final long serialVersionUID = 11L;
    private EmployeeDTO employee;

    public LogoutRequest(EmployeeDTO employee) {
        this.employee = employee;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }
}
