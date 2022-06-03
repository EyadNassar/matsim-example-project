package org.matsim.munich;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;

public class CleanNetwork {
    public static void main(String[] args) {
        Config config = ConfigUtils.createConfig();
        config.network().setInputFile("scenarios/munich/munichNetwork.xml.gz");
        Scenario munich = ScenarioUtils.loadScenario(config) ;
        Network network = munich.getNetwork();
        NetworkUtils.runNetworkSimplifier(network);
        NetworkUtils.runNetworkCleaner(network);
        new NetworkWriter(network).write("scenarios/munich/munichNetwork.xml.gz");
    }
}
