package org.matsim.miniCity;

import org.apache.commons.compress.utils.Sets;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.NetworkWriter;

public class CreateNetwork {
    private static final long CAPACITY_CAR = 50;
    private static final long CAPACITY_TRAIN = 20;

    private static final double SPEED_CAR = 13.88;
    private static final double SPEED_TRAIN= 22.22;


    public static void main(String[] args) {

        Network network = NetworkUtils.createNetwork();
        NetworkFactory factory = network.getFactory();

        Node node0 = factory.createNode(Id.createNodeId(0), new Coord(0, 0));
        network.addNode(node0);
        Node node1 = factory.createNode(Id.createNodeId(1), new Coord(1000, 500));
        network.addNode(node1);
        Node node2 = factory.createNode(Id.createNodeId(2), new Coord(1000, -500));
        network.addNode(node2);
        Node node3 = factory.createNode(Id.createNodeId(3), new Coord(2000, 0));
        network.addNode(node3);



        Link link01 = factory.createLink(Id.createLinkId("0_1"), node0, node1);
        network.addLink(link01);
        setCarLinkAttributes(link01);
        Link link10 = factory.createLink(Id.createLinkId("1_0"), node1, node0);
        network.addLink(link10);
        setCarLinkAttributes(link10);


        Link link12 = factory.createLink(Id.createLinkId("0_2"), node0, node2);
        network.addLink(link12);
        setCarLinkAttributes(link12);
        Link link21 = factory.createLink(Id.createLinkId("2_0"), node2, node0);
        network.addLink(link21);
        setCarLinkAttributes(link21);


        Link link13 = factory.createLink(Id.createLinkId("1_3"), node1, node3);
        network.addLink(link13);
        setCarLinkAttributes(link13);
        Link link31 = factory.createLink(Id.createLinkId("3_1"), node3, node1);
        network.addLink(link31);
        setCarLinkAttributes(link31);


        Link link23 = factory.createLink(Id.createLinkId("2_3"), node2, node3);
        network.addLink(link23);
        setCarLinkAttributes(link23);
        Link link32 = factory.createLink(Id.createLinkId("3_2"), node3, node2);
        network.addLink(link32);
        setCarLinkAttributes(link32);


        Link link03 = factory.createLink(Id.createLinkId("0_3"), node0, node3);
        network.addLink(link03);
        setPTLinkAttributes(link03);

        Link link30 = factory.createLink(Id.createLinkId("3_0"), node3, node0);
        network.addLink(link30);
        setPTLinkAttributes(link30);


        new NetworkWriter(network).write("scenarios/simpleCity/network.xml");
    }

    private static void setPTLinkAttributes(Link link) {
        link.setCapacity(CAPACITY_TRAIN);
        link.setFreespeed(SPEED_TRAIN);
        link.setAllowedModes(Sets.newHashSet(TransportMode.train));
    }

    private static void setCarLinkAttributes(Link link) {
        link.setCapacity(CAPACITY_CAR);
        link.setFreespeed(SPEED_CAR);
        link.setAllowedModes(Sets.newHashSet(TransportMode.car));
    }
}
