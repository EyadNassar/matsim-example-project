package org.matsim.miniCity;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;


public class CreatePopulation {
    final static int POPULATION = 500;
    public static void main(String[] args) {
        Config config = ConfigUtils.createConfig();
        Population population = PopulationUtils.createPopulation(config);
        PopulationFactory factory = population.getFactory();

        for (int i = 0; i < POPULATION; i++){

            Person person = factory.createPerson(Id.createPersonId(i));
            Plan plan = factory.createPlan();


            Coord homeCoordinate = new Coord(0, 0);
            Activity activity1 = factory.createActivityFromCoord("home", homeCoordinate);
            activity1.setEndTime(8*60*60+Math.random()*60*60);
            plan.addActivity(activity1);


            Leg leg = factory.createLeg(TransportMode.car);
            plan.addLeg(leg);

            Coord workCoordinate = new Coord(2000,0);
            Activity activity2 = factory.createActivityFromCoord("work", workCoordinate);
            activity2.setEndTime(16*60*60 +Math.random()*60*60);
            plan.addActivity(activity2);


            Leg leg2 = factory.createLeg(TransportMode.car);
            plan.addLeg(leg2);


            Activity activity3 = factory.createActivityFromCoord("home", homeCoordinate);
            plan.addActivity(activity3);

            person.addPlan(plan);
            population.addPerson(person);
        }

        PopulationWriter writer = new PopulationWriter(population);
        writer.write("scenarios/simpleCity/population.xml");
    }
}
