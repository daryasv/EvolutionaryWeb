package chatEngine.tasks;

import engine.Evolutionary;
import engine.models.EndCondition;
import engine.models.EngineProgressInterface;
import engine.models.SolutionFitness;
import javafx.concurrent.Task;
import models.Lesson;
import models.TimeTableDataSet;
import models.evolution.EvolutionConfig;

import java.util.List;

public class RunEvolutionaryTask implements Runnable {

    TimeTableDataSet timeTableDataSet;
    EvolutionConfig evolutionConfig;
    EndCondition endCondition;
    private final int interval;
    private Evolutionary<Lesson> evolutionary = null;
    double percentage;
    boolean finished = false;
    boolean running = false;

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
        percentage = 0;
    }

    @Override
    public void run() {
        try {
            finished = false;
            running = true;
            evolutionary = new Evolutionary<>();
            timeTableDataSet.setGenerationsInterval(interval);
            evolutionary.run(timeTableDataSet, endCondition, new EngineProgressInterface() {
                @Override
                public void update(double work, double done) {
                    if(done !=0) {
                        percentage = (work / done) * 100;
                    }
                }
            });
            finished = true;
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
            finished = true;
            running = false;
        }

    }

    public void stopAlgo(){
        evolutionary.stop();
    }

    public double getPercentage() {
        return this.percentage;
    }

    public boolean isFinished(){
        return this.finished;
    }

    public boolean isRunning(){
        return this.running;
    }

    public SolutionFitness<Lesson> getGlobalSolution(){
        return evolutionary.getGlobalBestSolution();
    }

    public List<SolutionFitness<Lesson>> getBestSolution(){
        return evolutionary.getBestSolutions();
    }
}