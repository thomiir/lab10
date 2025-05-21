package transportAgency.protobufprotocol;


public class ProtobufUtils {

    public static Response createOkResponse() {
        return Response.newBuilder().setType(Response.Type.Ok).build();
    }

    public static Response createErrorResponse(String text) {
        return Response.newBuilder().setType(Response.Type.Error).setError(text).build();
    }

    public static Response createFindSeatsResponse(transportAgency.model.Seat[] seats) {
        Response.Builder response = Response.newBuilder().setType(Response.Type.FindSeats);
        for (transportAgency.model.Seat seat : seats) {
            Seat seatDTO = Seat.newBuilder().setSeatNo(seat.seatNo()).setClientName(seat.clientName()).build();
            response.addSeats(seatDTO);
        }
        return response.build();
    }

    public static Response createFindTripsResponse(transportAgency.model.Trip[] trips) {
        Response.Builder response = Response.newBuilder().setType(Response.Type.FindTrips);
        for (transportAgency.model.Trip trip : trips) {
            Trip tripDTO = Trip.newBuilder()
                    .setId(trip.getId())
                    .setDestination(trip.getDestination())
                    .setDepartureDate(trip.getDepartureDate().toString())
                    .setDepartureTime(trip.getDepartureTime().toString())
                    .setNoSeatsAvailable(trip.getNoSeatsAvailable())
                    .build();
            response.addTrips(tripDTO);
        }
        return response.build();
    }

    public static Response createMakeReservationResponse(transportAgency.model.Reservation reservation) {
        Response.Builder response = Response.newBuilder().setType(Response.Type.MakeReservation);
        Trip tripDTO = Trip.newBuilder()
                .setId(reservation.getTrip().getId())
                .setDestination(reservation.getTrip().getDestination())
                .setDepartureDate(reservation.getTrip().getDepartureDate().toString())
                .setDepartureTime(reservation.getTrip().getDepartureTime().toString())
                .setNoSeatsAvailable(reservation.getTrip().getNoSeatsAvailable())
                .build();
        Reservation reservationDTO = Reservation.newBuilder()
                .setClientName(reservation.getClientName())
                .setNoSeats(reservation.getNoSeats())
                .setTrip(tripDTO)
                .build();
        response.setReservation(reservationDTO);
        return response.build();
    }

    public static Response createFindEmployeeResponse(transportAgency.model.Employee employee) {
        Response.Builder response = Response.newBuilder().setType(Response.Type.FindEmployee);
        Employee employeeDTO = Employee.newBuilder()
                .setUsername(employee.getUsername())
                .setPassword(employee.getPassword())
                .build();
        response.setEmployee(employeeDTO);
        return response.build();
    }

    public static Response createFindTripResponse(transportAgency.model.Trip trip) {
        Response.Builder response = Response.newBuilder().setType(Response.Type.FindTrip);
        Trip tripDTO = Trip.newBuilder()
                .setId(trip.getId())
                .setDestination(trip.getDestination())
                .setDepartureDate(trip.getDepartureDate().toString())
                .setDepartureTime(trip.getDepartureTime().toString())
                .setNoSeatsAvailable(trip.getNoSeatsAvailable())
                .build();
        response.setTrip(tripDTO);
        return response.build();
    }
}
