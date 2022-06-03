package org.matsim.miniCity;

import org.apache.commons.compress.utils.Sets;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;

public class CreateConfig {
    public static void main(String[] args) {
        Config config = ConfigUtils.createConfig();
        config.controler().setLastIteration(50);
        config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );
        config.controler().setOutputDirectory("scenarios/simpleCity/output");
        config.controler().setWriteEventsInterval(5);
        config.network().setInputFile("network.xml");
        config.plans().setInputFile("population.xml");
        config.qsim().setStartTime(4*60*60);
        config.qsim().setEndTime(26*60*60);
        config.qsim().setMainModes(Sets.newHashSet(TransportMode.car,TransportMode.train));
//        config.qsim().setFlowCapFactor(0.01);
//        config.qsim().setStorageCapFactor(0.01);

        config.transit(). setUseTransit(true);
        config.transit().setTransitScheduleFile("trainSchedule.xml");
        config.transit().setVehiclesFile("trainVehicles.xml");
        config.transit().setTransitModes(Sets.newHashSet(TransportMode.pt));


        PlanCalcScoreConfigGroup.ActivityParams home = new PlanCalcScoreConfigGroup.ActivityParams("home");
        home.setTypicalDuration(12 * 60 * 60);
        config.planCalcScore().addActivityParams(home);
        PlanCalcScoreConfigGroup.ActivityParams work = new PlanCalcScoreConfigGroup.ActivityParams("work");
        work.setTypicalDuration(8 * 60 * 60);
        config.planCalcScore().addActivityParams(work);

        StrategyConfigGroup.StrategySettings strategy1 = new StrategyConfigGroup.StrategySettings();
        strategy1.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute);
        strategy1.setWeight(0.10);
        config.strategy().addStrategySettings(strategy1);

        StrategyConfigGroup.StrategySettings strategy2 = new StrategyConfigGroup.StrategySettings();
        strategy2.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta);
        strategy2.setWeight(0.60);
        config.strategy().addStrategySettings(strategy2);

        StrategyConfigGroup.StrategySettings strategy3 = new StrategyConfigGroup.StrategySettings();
        strategy3.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode);
        strategy3.setWeight(0.30);
        config.strategy().addStrategySettings(strategy3);

        config.strategy().setMaxAgentPlanMemorySize(5);
        config.strategy().setFractionOfIterationsToDisableInnovation(0.8);
        config.vspExperimental().setWritingOutputEvents(true);
       ConfigWriter writer = new ConfigWriter(config);
        writer.write("scenarios/simpleCity/config.xml");

    }
}
