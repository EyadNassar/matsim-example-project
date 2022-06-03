package org.matsim.miniCity;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.pt.transitSchedule.TransitScheduleFactoryImpl;
import org.matsim.pt.transitSchedule.api.*;

import java.util.ArrayList;
import java.util.List;

public class CreateTrainSchedule {
    public static void main(String[] args) {

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile("scenarios/simpleCity/network.xml");

        //create factory for schedule objects
        TransitScheduleFactory transitScheduleFactory = new TransitScheduleFactoryImpl();

        //create actual schedule
        TransitSchedule transitSchedule = transitScheduleFactory.createTransitSchedule();

        //create stop facilities
        Coord coordinatesStart = new Coord(0, 0);
        Coord coordinatesEnd = new Coord(2000, 0);

        TransitStopFacility startStation =
                transitScheduleFactory.createTransitStopFacility(Id.create("node0", TransitStopFacility.class),
                        coordinatesStart, false);

        TransitStopFacility endStation =
                transitScheduleFactory.createTransitStopFacility(Id.create("node3", TransitStopFacility.class),
                        coordinatesEnd, false);

        //set in-routeLinks
        startStation.setLinkId(Id.createLinkId("3_0"));
        endStation.setLinkId(Id.createLinkId("0_3"));

        //add stop facilities to schedule
        transitSchedule.addStopFacility(startStation);
        transitSchedule.addStopFacility(endStation);

        //create a transit line
        TransitLine trainLine = transitScheduleFactory.createTransitLine(Id.create("trainLine", TransitLine.class));

        //create a transit route
        List<Id<Link>> routeLinks = new ArrayList<>();


        routeLinks.add(Id.createLinkId("3_0"));
        routeLinks.add(Id.createLinkId("0_3"));
        routeLinks.add(Id.createLinkId("3_0"));

        //create network route
        NetworkRoute route = RouteUtils.createNetworkRoute(routeLinks);

        //create route stops with scheduled arrival/departure offsets (=travelTime from first stop)
        List<TransitRouteStop> stopsRoute = new ArrayList<>();

        double routeTravelTime = computeTravelTime(route, network);

        TransitRouteStop transitRouteStop1 = transitScheduleFactory.createTransitRouteStop(startStation, 0, 0);
        TransitRouteStop transitRouteStop2 = transitScheduleFactory.createTransitRouteStop(endStation, routeTravelTime/2, routeTravelTime/2);
        TransitRouteStop transitRouteStop3 = transitScheduleFactory.createTransitRouteStop(startStation, routeTravelTime , routeTravelTime );

        stopsRoute.add(transitRouteStop1);
        stopsRoute.add(transitRouteStop2);
        stopsRoute.add(transitRouteStop3);


        TransitRoute trainRoute = transitScheduleFactory.createTransitRoute(Id.create("trainRoute", TransitRoute.class),
                route, stopsRoute, TransportMode.train);

        //add departures
        for(int i = 8 * 3600; i < 18 * 3600; i+= 5 * 60) {
            Departure departure = transitScheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
            departure.setVehicleId(Id.createVehicleId("departure_" + i));
            trainRoute.addDeparture(departure);
        }

        //add route to line
        trainLine.addRoute(trainRoute);

        //add line to schedule
        transitSchedule.addTransitLine(trainLine);

        new TransitScheduleWriter(transitSchedule).writeFile("scenarios/simpleCity/trainSchedule.xml");
    }

    private static double computeTravelTime(NetworkRoute route, Network network) {
        double travelTime = 0;
        for (Id<Link> linkId : route.getLinkIds()) {
            if (linkId.equals(route.getStartLinkId())) {
                break;
            }
            travelTime += NetworkUtils.getFreespeedTravelTime(network.getLinks().get(linkId));
        }
        return travelTime;
    }
}
