package chatEngine.evolution;

import engine.Evolutionary;
import engine.models.SolutionFitness;
import models.Lesson;
import models.TimeTableDataSet;
import models.evolution.EvolutionConfig;

import java.util.ArrayList;
import java.util.List;

public class EvolutionaryTaskMembers {
    EvolutionProblem evolutionProblem;
    SolutionFitness<Lesson> globalBestSolution;
    List<SolutionFitness<Lesson>> bestSolutions;
    Evolutionary<Lesson> evolutionary;

    public EvolutionaryTaskMembers() {
        evolutionary = new Evolutionary<>();
    }

    public EvolutionConfig getEvolutionEngineDataSet() {
        return this.evolutionProblem.evolutionEngineDataSet;
    }

    public TimeTableDataSet getTimeTable() {
        return evolutionProblem.getTimeTable();
    }

    public void setTimeTable(TimeTableDataSet timeTable) {
        this.evolutionProblem.timeTable = timeTable;
    }

    public void setEvolutionEngineDataSet(EvolutionConfig evolutionEngineDataSet) {
        this.evolutionProblem.evolutionEngineDataSet = evolutionEngineDataSet;
    }

    public SolutionFitness<Lesson> getGlobalBestSolution() {
        return globalBestSolution;
    }

    public void setGlobalBestSolution(SolutionFitness<Lesson> globalBestSolution) {
        this.globalBestSolution = globalBestSolution;
    }

    public List<SolutionFitness<Lesson>> getBestSolutions() {
        return bestSolutions;
    }

    public void setBestSolutions(List<SolutionFitness<Lesson>> bestSolutions) {
        this.bestSolutions = bestSolutions;
    }

    public Evolutionary<Lesson> getEvolutionary() {
        return evolutionary;
    }

    public void setEvolutionary(Evolutionary<Lesson> evolutionary) {
        this.evolutionary = evolutionary;
    }

    public void reset() {
        bestSolutions = new ArrayList<>();
        evolutionary = new Evolutionary<>();
        globalBestSolution = null;
    }
}
