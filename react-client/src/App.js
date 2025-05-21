import {useEffect, useState} from "react";
import CryptoJS from "crypto-js"

function encryptPassword(plaintext) {
    const key = CryptoJS.enc.Utf8.parse("b14ca5898a4e4133bbce2ea2315a1916".padEnd(32, "0"));
    const iv = CryptoJS.enc.Utf8.parse("Kk7V9e3fQw2p1lzT".padEnd(16, "0"));

    const encrypted = CryptoJS.AES.encrypt(plaintext, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });

    return encrypted.toString();
}

function App() {
    const [trips, setTrips] = useState([]);
    const [searchedTrip, setSearchedTrip] = useState(null);
    const [id, setId] = useState(-1);
    const [addDestination, setAddDestination] = useState(null);
    const [addDepartureDate, setAddDepartureDate] = useState(null);
    const [addDepartureTime, setAddDepartureTime] = useState(null);
    const [updateDestination, setUpdateDestination] = useState(null);
    const [updateDepartureDate, setUpdateDepartureDate] = useState(null);
    const [updateDepartureTime, setUpdateDepartureTime] = useState(null);
    const [updateNoSeatsAvailable, setUpdateNoSeatsAvailable] = useState(null);
    const [updateId, setUpdateId] = useState(null);
    const [deleteId, setDeleteId] = useState(null);
    const [token, setToken] = useState(null);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const addr = "http://localhost:8080/api"


    useEffect( () => {
        fetch(`${addr}/trips`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        })
            .then(res => res.json())
            .then(json => setTrips(json));
    }, [token]);

    if (token == null) {
        let encrypted = encryptPassword(password);
        console.log(encrypted);
        return (
            <div>
                <form onSubmit={(event) => {
                    event.preventDefault();
                    fetch(`${addr}/login`, {
                        method: 'POST',
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({
                            "username" : username,
                            "password" : encrypted
                        })
                    })
                        .then(res => {
                            if (!res.ok)
                                throw new Error(`Http err, status ${res.status}`);
                            return res.text();
                        })
                        .then(json => setToken(json))
                        .catch(error => window.alert(error));
                }} style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    marginTop: '3%'
                }}>
                    <label>Login</label>
                    <input type="text" placeholder="username" onChange={event => setUsername(event.target.value)}/>
                    <input type="password" placeholder="password" onChange={event => setPassword(event.target.value)}/>
                    <input type="submit"/>
                </form>
            </div>
        );
    }

    return (
        <div style={{display: 'flex'}}>
            <div style={{flex: 3}}>
                <table className="table">
                    <thead>
                    <tr className="header">
                        <td>No.</td>
                        <td>Destination</td>
                        <td>Date</td>
                        <td>Time</td>
                        <td>Seats available</td>
                    </tr>
                    </thead>
                    <tbody className="body">
                    {trips.map((trip) => (
                        <tr key={trip.id}>
                            <td>{trip.id}</td>
                            <td>{trip.destination}</td>
                            <td>{trip.departureDate}</td>
                            <td>{trip.departureTime}</td>
                            <td>{trip.noSeatsAvailable}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <label style={{margin: '5%'}}>Last searched trip: {searchedTrip?.id || ""} {searchedTrip?.destination || ""} {searchedTrip?.departureDate || ""} {searchedTrip?.departureTime || ""} {searchedTrip?.noSeatsAvailable || ""}</label>
            </div>
            <div style={{flex: 1, display: 'flex', flexDirection: 'column', marginRight: '5%'}}>

                {/* get by id*/}
                <form onSubmit = { (event) => {
                    event.preventDefault();
                    fetch(`${addr}/trips/${id}`, {
                        method: 'GET',
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        }
                    })
                        .then(res => {
                            if (!res.ok)
                                throw new Error(`Http err, status ${res.status}`);
                            return res.json();
                        })
                        .then(json => setSearchedTrip(json))
                        .catch(error => window.alert(error));
                }} className="form">
                    <label>Search trip by id:</label>
                    <input type="number" placeholder="Id:" onChange={(event) => setId(event.target.value)}/>
                    <input type="submit"/>
                </form>

                {/* create a new trip */}
                <form onSubmit = { (event) => {
                    event.preventDefault();
                    const departureTime = addDepartureTime + ':00';
                    let trip = {destination: addDestination, departureDate: addDepartureDate, departureTime: departureTime, noSeatsAvailable: 18};
                    fetch(`${addr}/trips`, {
                        method: 'POST',
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        },
                        body: JSON.stringify(trip)
                    })
                        .then(res => {
                            if (!res.ok)
                                throw new Error(`Http err, status ${res.status}`);
                            return res.json();
                        })
                        .then(json => {
                            setTrips(prev => [...prev, json])
                            window.alert('trip created successfully!');
                        })
                        .catch(error => window.alert(error));
                }} className="form">
                    <label>Add trip:</label>
                    <input type="text" placeholder="destination:" onChange={(event) => setAddDestination(event.target.value)}/>
                    <input type="date" onChange={(event => setAddDepartureDate(event.target.value))}/>
                    <input type="time" onChange={(event) => setAddDepartureTime(event.target.value)}/>
                    <input type="submit"/>
                </form>

                {/* updating a trip */}
                <form onSubmit = {event => {
                    event.preventDefault();
                    const departureTime = updateDepartureTime + ':00';
                    let trip = {destination: updateDestination, departureDate: updateDepartureDate, departureTime: departureTime, noSeatsAvailable: updateNoSeatsAvailable};
                    fetch(`${addr}/trips/${updateId}`, {
                        method: 'PUT',
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        },
                        body: JSON.stringify(trip)
                    })
                        .then(res => {
                            if (!res.ok)
                                throw new Error(`Http err, status ${res.status}`);
                            return res.json();
                        })
                        .then(json => {
                            setTrips(prev => prev.map(trip =>
                                trip.id === json.id ? json : trip
                            ));
                            window.alert('updated successfully')
                        })
                        .catch(error => window.alert(error));
                }} className="form">
                    <label>Update trip:</label>
                    <input type="number" placeholder="trip id:" onChange={event => setUpdateId(event.target.value)}/>
                    <input type="text" placeholder="destination:" onChange={event => setUpdateDestination(event.target.value)}/>
                    <input type="date" onChange={event => setUpdateDepartureDate(event.target.value)}/>
                    <input type="time" onChange={event => setUpdateDepartureTime(event.target.value)}/>
                    <input type="number" placeholder="no. seats:" onChange={event => setUpdateNoSeatsAvailable(event.target.value)}/>
                    <input type="submit"/>
                </form>

                {/* deleting a trip */}
                <form  onSubmit = {event => {
                    event.preventDefault();
                    fetch(`${addr}/trips/${deleteId}`, {
                        method: 'DELETE',
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        },
                    })
                        .then(res => {
                            if (!res.ok)
                                throw new Error(`Http err, status ${res.status}`);
                            return res;
                        })
                        .then(_ => {
                            setTrips(prev => prev.filter(trip => trip.id !== deleteId));
                            window.alert('deleted successfully');
                        })
                        .catch(error => window.alert(error));
                }} className="form">
                    <label>Delete trip:</label>
                    <input type="number" placeholder="trip id:" onChange={event => setDeleteId(Number(event.target.value))}/>
                    <input type="submit"/>
                </form>
            </div>
        </div>
    );
}

export default App;


