package transportAgency.objectprotocol;

import transportAgency.dto.EmployeeDTO;

public class LogoutRequest implements Request {
    private EmployeeDTO employee;

    public LogoutRequest(EmployeeDTO employee) {
        this.employee = employee;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }
}
