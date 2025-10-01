package org.example.timetableTest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimetableResponse {
    @JsonProperty("departure_station")
    private Station departureStation;

    @JsonProperty("arrival_station")
    private Station arrivalStation;

    private Map<String, Train> trains;

    public Station getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(Station departureStation) {
        this.departureStation = departureStation;
    }

    public Station getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(Station arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public Map<String, Train> getTrains() {
        return trains;
    }

    public void setTrains(Map<String, Train> trains) {
        this.trains = trains;
    }
}
