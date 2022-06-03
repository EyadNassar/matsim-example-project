package org.matsim.munich;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreatePopulation {
    private static final String ZONE_FILE = "scenarios/munich/input/georef-germany-kreis-millesime.shp";
    private static final String ZONE_ID = "krs_code";
    private static final String OUTPUT_FILE = "scenarios/munich/input/munichPopulation.xml";
    private static final double SCALE_FACTOR = 0.01;
    private static final double PT_SHARE = 0.35;
    private static final CoordinateTransformation ct = TransformationFactory.
            getCoordinateTransformation(TransformationFactory.WGS84, TransformationFactory.DHDN_GK4);

    private static final Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
    private static Map<String, Geometry> shapeMap;

    public static void main(String[] args) {
        shapeMap = readShapeFile(ZONE_FILE, ZONE_ID);

        createOD(471859, "09162", "09162", "Munich City-Munich City");

        PopulationWriter pw = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
        pw.write(OUTPUT_FILE);
    }

    private static void createOD(int commuter, String origin, String destination, String odPrefix) {
        Geometry home = shapeMap.get(origin);
        Geometry work = shapeMap.get(destination);

        int scaledCommuter = (int) (commuter * SCALE_FACTOR);

        for (int i = 1; i <= scaledCommuter; i++) {

            String mode;

            if (Math.random() >= PT_SHARE) {
                mode = "car";
            } else {
                mode = "pt";
            }

            Coord homeCoord = drawRandomPointFromGeometry(home);
            homeCoord = ct.transform(homeCoord);

            Coord workCoord = drawRandomPointFromGeometry(work);
            workCoord = ct.transform(workCoord);

            createOneCommuter(i, homeCoord, workCoord, mode, odPrefix);
        }
    }

    private static void createOneCommuter(int i, Coord homeCoord, Coord workCoord, String mode, String odPrefix) {
        double departureTimeVariance = Math.random() * 60 * 60;
        double durationTimeVariance = Math.random() * 60 * 60;

        Id<Person> personId = Id.createPersonId(odPrefix + "_" + i);
        Person person = scenario.getPopulation().getFactory().createPerson(personId);
        scenario.getPopulation().addPerson(person);

        Plan plan = scenario.getPopulation().getFactory().createPlan();
        person.addPlan(plan);

        Activity home = scenario.getPopulation().getFactory().createActivityFromCoord("home", homeCoord);
        home.setEndTime(9 * 60 * 60 + departureTimeVariance);
        plan.addActivity(home);

        Leg legToWork = scenario.getPopulation().getFactory().createLeg(mode);
        plan.addLeg(legToWork);

        Activity work = scenario.getPopulation().getFactory().createActivityFromCoord("work", workCoord);
        work.setMaximumDuration(8 * 60 * 60 + durationTimeVariance);
        plan.addActivity(work);

        Leg legToHome = scenario.getPopulation().getFactory().createLeg(mode);
        plan.addLeg(legToHome);

        Activity endAtHome = scenario.getPopulation().getFactory().createActivityFromCoord("home", homeCoord);
        plan.addActivity(endAtHome);
    }

    private static Coord drawRandomPointFromGeometry(Geometry g) {
        Random rmd = MatsimRandom.getLocalInstance();
        Point p;
        double x;
        double y;
        do {
            x = g.getEnvelopeInternal().getMinX()
                    + rmd.nextDouble() * (g.getEnvelopeInternal().getMaxX() - g.getEnvelopeInternal().getMinX());
            y = g.getEnvelopeInternal().getMinY()
                    + rmd.nextDouble() * (g.getEnvelopeInternal().getMaxY() - g.getEnvelopeInternal().getMinY());
            p = MGC.xy2Point(x, y);
        } while (g.contains(p));
        return new Coord(p.getX(), p.getY());
    }

    public static Map<String, Geometry> readShapeFile(String filename, String attrString) {
        Map<String, Geometry> shapeMap = new HashMap<>();
        for (SimpleFeature ft : ShapeFileReader.getAllFeatures(filename)) {
            GeometryFactory geometryFactory = new GeometryFactory();
            WKTReader wktReader = new WKTReader(geometryFactory);
            Geometry geometry;
            try {
                geometry = wktReader.read((ft.getAttribute("the_geom")).toString());
                shapeMap.put(ft.getAttribute(attrString).toString(), geometry);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return shapeMap;
    }
}
