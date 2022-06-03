package org.matsim.miniCity;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.vehicles.*;

public class CreateTrainVehicles {

    private static final int SEATS = 40;
    private static final int STANDING_ROAM= 40;
    private static final double MAX_VELOCITY = 33.33;


    public static void main(String[] args) {

        Vehicles vehicles = VehicleUtils.createVehiclesContainer();
        VehicleType type = VehicleUtils.createVehicleType(Id.create("train", VehicleType.class));
        vehicles.addVehicleType(type);
        type.getCapacity().setSeats(SEATS);
        type.getCapacity().setStandingRoom(STANDING_ROAM);
        type.setMaximumVelocity(MAX_VELOCITY);
        type.setNetworkMode(TransportMode.train);

        for(int i = 8 * 3600; i < 20 * 3600; i+= 5 * 60) {
            Vehicle vehicle = VehicleUtils.createVehicle(Id.createVehicleId("departure_" + i), type);
            vehicles.addVehicle(vehicle);
        }
        new MatsimVehicleWriter(vehicles).writeFile("scenarios/simpleCity/trainVehicles.xml");
    }
}
