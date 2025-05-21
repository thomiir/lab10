package transportAgency.rest.client;

import io.jsonwebtoken.lang.Supplier;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import transportAgency.model.Trip;
import transportAgency.rest.services.LoginRequest;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class Client {
    private String token;

    RestClient restClient = RestClient.builder().requestInterceptor(new CustomRestClientInterceptor(() -> token)).build();

    public static final String URL = "http://localhost:8080/api/trips";


    public void login(String username, String password) {
        var authClient = RestClient.create();
        var request = new LoginRequest(username, password);
        this.token = authClient.post()
                .uri("http://localhost:8080/api/login")
                .contentType(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }

    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Trip[] getAll() {
        return execute(() -> restClient.get().uri(URL).retrieve().body(Trip[].class));
    }

    public Trip getById(Long id) {
        return execute(() -> restClient.get(). uri(String.format("%s/%s", URL, id)).retrieve().body(Trip.class));
    }

    public Trip create(Trip trip) {
        return execute(() -> restClient.post().uri(URL).contentType(APPLICATION_JSON).body(trip).retrieve().body(Trip.class));
    }

    public void delete(Long id) {
        execute(() -> restClient.delete().uri(String.format("%s/%d", URL, id)).retrieve().toBodilessEntity());
    }

    public Trip update(Long id, Trip trip) {
        return execute(() -> restClient.put().uri(String.format("%s/%d", URL, id)).body(trip).retrieve().body(Trip.class));
    }

    public static class CustomRestClientInterceptor implements ClientHttpRequestInterceptor {
        private final Supplier<String> tokenSupplier;

        public CustomRestClientInterceptor(Supplier<String> tokenSupplier) {
            this.tokenSupplier = tokenSupplier;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
            System.out.println("Sending a "+request.getMethod()+ " request to "+request.getURI()+ " and body ["+new String(body)+"]");
            String token = tokenSupplier.get();
            if (token != null)
                request.getHeaders().setBearerAuth(token);

            ClientHttpResponse response=null;
            try {
                response = execution.execute(request, body);
                System.out.println("Got response code " + response.getStatusCode());
            }catch(IOException ex){
                System.err.println("Eroare executie "+ex);
            }
            return response;
        }
    }
}
