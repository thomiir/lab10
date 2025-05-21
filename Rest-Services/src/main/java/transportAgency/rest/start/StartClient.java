package transportAgency.rest.start;

import org.springframework.web.client.RestClientException;
import transportAgency.model.Trip;
import transportAgency.protobufprotocol.AESEncryptor;
import transportAgency.rest.client.Client;

import java.sql.Date;
import java.sql.Time;

public class StartClient {
    public static void main(String[] args) {
        try {
            Client tripClient = new Client();
            tripClient.login("username", AESEncryptor.encrypt("password"));

            // post
            Trip newTrip = new Trip("destinatie_test", Date.valueOf("2024-05-31"),
                    Time.valueOf("12:00:00"), 18);
            System.out.println("Adding a new trip " + newTrip);
            newTrip = tripClient.create(newTrip);
            System.out.println(newTrip);

            // get
            System.out.println("\nPrinting all trips ...");
            Trip[] allTrips = tripClient.getAll();
            for (Trip t : allTrips) {
                System.out.println(t.getId() + " " + t.getDestination() + " " +
                        t.getDepartureDate() + " " + t.getDepartureTime() +
                        " " + t.getNoSeatsAvailable());
            }

            // put
            System.out.println("\nUpdating trip with id=" + newTrip.getId());
            System.out.println(tripClient.update(newTrip.getId(), new Trip("destinationUpdate",
                    Date.valueOf("2025-05-17"), Time.valueOf("12:00:00"), 18)));

            // get
            System.out.println("\nInfo for trip with id=" + newTrip.getId());
            System.out.println(tripClient.getById(newTrip.getId()));

            // delete
            System.out.println("\nDeleting trip with id=" + newTrip.getId());
            tripClient.delete(newTrip.getId());

        } catch (RestClientException ex) {
            System.out.println("Exception ... " + ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}