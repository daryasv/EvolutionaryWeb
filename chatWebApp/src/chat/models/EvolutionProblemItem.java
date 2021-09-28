package chat.models;

import chatEngine.evolution.EvolutionProblem;
import engine.Evolutionary;
import engine.models.SolutionFitness;
import models.TimeTableDataSet;
import models.timeTable.Rule;
import models.timeTable.TimeTableMembers;

import java.util.Optional;

public class EvolutionProblemItem {
    String owner;
    int id;
    double bestFitness;
    int totalUsers;
    int days;
    int hours;
    int classes;
    int teachers;
    int subjects;
    int hardRules;
    int softRules;

    public EvolutionProblemItem(EvolutionProblem evolutionProblem){
        this.owner = evolutionProblem.getOwner();
        this.id = evolutionProblem.getId();
        this.totalUsers = evolutionProblem.getEvolutionRuns().size();
        this.bestFitness = 0;
        if(totalUsers > 0){
            Optional<Double> maxFitness = evolutionProblem.getEvolutionRuns().values().stream().map(x->x.getGlobalSolution().getFitness()).max(Double::compare);
            maxFitness.ifPresent(aDouble -> this.bestFitness = aDouble);
        }
        TimeTableMembers timeTableDataSet = evolutionProblem.getTimeTable().getTimeTableMembers();
        this.teachers = timeTableDataSet.getTeachers().size();
        this.classes = timeTableDataSet.getGrades().size();
        this.subjects = timeTableDataSet.getSubjects().size();
        this.hours = timeTableDataSet.getHours();
        this.days = timeTableDataSet.getDays();
        this.hardRules = (int)timeTableDataSet.getRules().stream().filter(Rule::isHard).count();
        this.softRules = (int)timeTableDataSet.getRules().stream().filter(x->!x.isHard()).count();
    }

}
