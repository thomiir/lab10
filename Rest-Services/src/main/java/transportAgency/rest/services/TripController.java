package transportAgency.rest.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transportAgency.model.Trip;
import transportAgency.persistence.jdbc.TripRepository;

import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "http://localhost:3000")
public class TripController {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private HttpServletRequest request;

    private void validateJwt() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        JwtUtils.validateToken(token);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Trip[] getAll() {
        validateJwt();
        return StreamSupport.stream(tripRepository.findAll().spliterator(), false).toArray(Trip[]::new);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable Long id) {
        validateJwt();
        Trip trip = tripRepository.findOne(id);
        if (trip == null)
            return new ResponseEntity<>("Trip not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(trip, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Trip create(@RequestBody Trip trip) {
        validateJwt();
        return tripRepository.save(trip);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestBody Trip trip, @PathVariable Long id) {
        validateJwt();
        if (tripRepository.findOne(id) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(tripRepository.update(id, trip), HttpStatus.OK);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id){
        validateJwt();
        if (tripRepository.findOne(id) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        tripRepository.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String userError(RuntimeException e) {
        return e.getMessage();
    }

}
