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
    EndCondition endCondition;
    private int interval;
    private Evolutionary<Lesson> evolutionary = null;
    double percentage;
    boolean finished = false;
    boolean running = false;
    boolean pause = false;

    public RunEvolutionaryTask(TimeTableDataSet timeTableDataSet,EvolutionConfig evolutionConfig, String endConditionType, double limit, int interval) {
        this.timeTableDataSet = timeTableDataSet;
        this.timeTableDataSet.setEvolutionConfig(evolutionConfig);

        percentage = 0;
    }

    @Override
    public void run() {
        try {
            finished = false;
            running = true;
            if(!pause || evolutionary == null) {
                evolutionary = new Evolutionary<>();
            }
            EndCondition.EndConditionType endConditionTypeEnum = timeTableDataSet.getEvolutionConfig().getEndCondition();
            this.interval = timeTableDataSet.getEvolutionConfig().getInterval();
            timeTableDataSet.setGenerationsInterval(interval);
            double limit =  timeTableDataSet.getEvolutionConfig().getLimit();
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
            pause = false;
            evolutionary.run(timeTableDataSet, endCondition, new EngineProgressInterface() {
                @Override
                public void update(double work, double done) {
                    if(done !=0) {
                        percentage = (work / done) * 100;
                    }
                }
            });
            if(!pause) {
                finished = true;
                running = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            finished = true;
            running = false;
        }

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
        if(evolutionary !=null) {
            return evolutionary.getGlobalBestSolution();
        }
        return null;
    }

    public List<SolutionFitness<Lesson>> getBestSolution(){
        return evolutionary.getBestSolutions();
    }

    public EvolutionConfig getEvolutionConfig(){
        return this.timeTableDataSet.getEvolutionConfig();
    }

    public synchronized void pause() {
        if(!finished) {
            pause = true;
            evolutionary.stop();
            running = false;
        }
    }

    public void stop(){
        if(!finished) {
            pause = false;
            evolutionary.stop();
            running = false;
            finished = true;
        }
    }

    public boolean isPaused() {
        return pause;
    }

    public Integer getCurrentGeneration() {
        if(evolutionary !=null){
            return evolutionary.getCurrentGeneration();
        }
        return null;
    }

    public Double getCurrentBestFitness() {
        if(evolutionary !=null){
            return evolutionary.getGlobalBestSolution().getFitness();
        }
        return null;
    }

    public void setEvolutionConfig(EvolutionConfig evolutionConfig) {
        timeTableDataSet.setEvolutionConfig(evolutionConfig);
    }
}