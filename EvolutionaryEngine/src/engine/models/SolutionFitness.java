package engine.models;

import java.io.Serializable;
import java.util.HashMap;

public class SolutionFitness<T> implements Serializable {
    private Solution<T> solution;
    private double fitness;
    private HashMap<IRule, Double> rulesFitness;
    private double hardRulesAvg;
    private double softRulesAvg;

    public SolutionFitness(Solution<T> solution, double fitness, HashMap<IRule, Double> rulesFitness, double hardRulesAvg, double softRulesAvg) {
        this.solution = solution;
        this.fitness = fitness;
        this.rulesFitness = rulesFitness;
        this.hardRulesAvg = hardRulesAvg;
        this.softRulesAvg = softRulesAvg;
    }



    public Solution<T> getSolution() {
        return solution;
    }

    public double getFitness() {
        return fitness;
    }

    public HashMap<IRule, Double> getRulesFitness() {
        return rulesFitness;
    }

    public double getHardRulesAvg() {
        return hardRulesAvg;
    }

    public double getSoftRulesAvg() {
        return softRulesAvg;
    }


    public int compareTo(SolutionFitness<T> sol2){
        if(this.getFitness() == sol2.getFitness()){
            return 0;
        }else if(this.getFitness() > sol2.getFitness()){
            return 1;
        }else{
            return -1;
        }
    }
}
