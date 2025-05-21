package transportAgency.rest.services;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transportAgency.persistence.hibernate.EmployeeRepository;
import java.sql.Date;

@RestController
@RequestMapping("/api/login")
public class EmployeeController {
    private static final long EXPIRATION_TIME = 360000;

    @Autowired
    private EmployeeRepository employeeRepository;

    public static String generateToken(String username) {
        String token =  Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(JwtUtils.getKEY(), Jwts.SIG.HS512)
                .compact()
                .trim();
        System.out.println(token);
        return token;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println(request);
        if (employeeRepository.findByUsernameAndPassword(request.username(), request.password()) != null) {
            String token = generateToken(request.username());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

}
