package transportAgency.objectprotocol;

import transportAgency.dto.EmployeeDTO;

public record LoginRequest(EmployeeDTO employee) implements Request {}
