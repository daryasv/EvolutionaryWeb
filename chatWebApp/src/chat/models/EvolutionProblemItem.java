package chat.models;

import chatEngine.evolution.EvolutionProblem;
import engine.Evolutionary;
import engine.models.SolutionFitness;
import models.TimeTableDataSet;

import java.util.Optional;

public class EvolutionProblemItem {
    String owner;
    int id;
    double bestFitness;
    int totalUsers;

    public EvolutionProblemItem(EvolutionProblem evolutionProblem){
        owner = evolutionProblem.getOwner();
        id = evolutionProblem.getId();
        totalUsers = 0;
        totalUsers = evolutionProblem.getEvolutionRuns().size();
        this.bestFitness = 0;//todo: set best
    }

}
