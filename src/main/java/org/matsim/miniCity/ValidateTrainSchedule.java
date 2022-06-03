package org.matsim.miniCity;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.utils.TransitScheduleValidator;

public class ValidateTrainSchedule {
    public static void main(String[] args) {
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        TransitSchedule schedule = scenario.getTransitSchedule();
        new TransitScheduleReader(scenario).readFile("scenarios/simpleCity/trainSchedule.xml");
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile("scenarios/simpleCity/network.xml");
        TransitScheduleValidator.ValidationResult validationResult
                = TransitScheduleValidator. validateAll(schedule, network);
        for (String warning : validationResult.getWarnings()) {
            System.out.println(warning);
        }
        for (String error : validationResult.getErrors()) {
            System.out.println(error);
        }
    }
}
