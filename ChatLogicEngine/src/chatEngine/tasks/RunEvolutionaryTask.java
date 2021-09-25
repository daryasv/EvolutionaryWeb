package chatEngine.tasks;

import engine.Evolutionary;
import engine.models.EndCondition;
import javafx.concurrent.Task;
import models.Lesson;
import models.TimeTableDataSet;
import models.evolution.EvolutionConfig;

public class RunEvolutionaryTask implements Runnable {

    TimeTableDataSet timeTableDataSet;
    EvolutionConfig evolutionConfig;
    EndCondition endCondition;
    private final int interval;
    private Evolutionary<Lesson> evolutionary = null;

    public RunEvolutionaryTask(TimeTableDataSet timeTableDataSet,EvolutionConfig evolutionConfig, String endConditionType, double limit, int interval) {
        this.timeTableDataSet = timeTableDataSet;
        this.evolutionConfig = evolutionConfig;
        EndCondition.EndConditionType endConditionTypeEnum = EndCondition.EndConditionType.valueOfLabel(endConditionType);
        this.interval = interval;
        endCondition = new EndCondition() {
            @Override
            public EndConditionType getEndCondition() {
                return endConditionTypeEnum;
            }

            @Override
            public double getLimit() {
                return limit;
            }
        };
    }

    @Override
    public void run() {
        try {
            evolutionary = new Evolutionary<>();
            timeTableDataSet.setGenerationsInterval(interval);
            evolutionary.run(timeTableDataSet,endCondition);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopAlgo(){
        evolutionary.stop();
    }
}