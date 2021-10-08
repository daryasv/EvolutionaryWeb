package chat.models;

import exception.ValidationException;
import models.evolution.Crossover;
import models.evolution.EvolutionConfig;
import models.evolution.Mutation;
import models.evolution.Selection;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class UserEvConfig {
    int populationSize;
    String selectionType;
    double selectionPercentage;
    int elitismSize;
    String crossoverType;
    int cuttingPoints;
    String orientationType;
    String endCondition;
    double endConditionLimit;
    int generationsInterval;
    List<Mutation> mutations;

    public UserEvConfig(EvolutionConfig evolutionConfig){
        if(evolutionConfig != null) {
            this.populationSize = evolutionConfig.getInitialPopulation();
            this.selectionType = evolutionConfig.getSelection().getType().toString();
            this.selectionPercentage = evolutionConfig.getSelection().getConfiguration();
            this.elitismSize = evolutionConfig.getSelection().getElitismCount();
            this.crossoverType = evolutionConfig.getCrossover().getName().toString();
            this.cuttingPoints = evolutionConfig.getCrossover().getCuttingPoints();
            this.orientationType = evolutionConfig.getCrossover().getConfiguration();
            this.mutations = evolutionConfig.getMutations();
        }
    }

    public UserEvConfig(HttpServletRequest request) throws ValidationException {
        try {
            populationSize = Integer.parseInt(request.getParameter("populationSize"));
        } catch (Exception e) {
            throw new ValidationException("Invalid population size");
        }
        try {
            selectionType = request.getParameter("selectionType");
        } catch (Exception e) {
            throw new ValidationException("Invalid selectionType");
        }
        try {
            selectionPercentage = Double.parseDouble(request.getParameter("selectionPercentage"));
        } catch (Exception e) {
            throw new ValidationException("Invalid selectionPercentage");
        }
        try {
            elitismSize = Integer.parseInt(request.getParameter("elitismSize"));
        } catch (Exception e) {
            throw new ValidationException("Invalid elitismSize");
        }
        try {
            crossoverType = request.getParameter("crossoverType");
        } catch (Exception e) {
            throw new ValidationException("Invalid crossoverType");
        }
        try {
            cuttingPoints = Integer.parseInt(request.getParameter("cuttingPoints"));
        } catch (Exception e) {
            throw new ValidationException("Invalid cuttingPoints");
        }
        try {
            orientationType = request.getParameter("orientationType");
        } catch (Exception e) {
            throw new ValidationException("Invalid orientationType");
        }
        try {
            endCondition = request.getParameter("endCondition");
        } catch (Exception e) {
            throw new ValidationException("Invalid endCondition");
        }
        try {
            endConditionLimit = Double.parseDouble(request.getParameter("endConditionLimit"));
        } catch (Exception e) {
            throw new ValidationException("Invalid endConditionLimit");
        }
        try {
            generationsInterval = Integer.parseInt(request.getParameter("generationsInterval"));
        } catch (Exception e) {
            throw new ValidationException("Invalid generationsInterval");
        }
        try{
            List<Mutation> mutations = new ArrayList<>();
            int count = 0;
            String mutationName = request.getParameter("mutations["+count+"][name]");
            while(mutationName!=null && !mutationName.isEmpty()){
                String probability = request.getParameter("mutations["+count+"][probability]");
                String maxTupples= request.getParameter("mutations["+count+"][maxTupples]");
                String component= request.getParameter("mutations["+count+"][component]");
                mutations.add(new Mutation(mutationName,probability,maxTupples,component));
                count++;
                mutationName = request.getParameter("mutations["+count+"][name]");
            }
            this.mutations = mutations;
        }catch (Exception e){
            throw new ValidationException("Invalid mutations");
        }
    }

    public EvolutionConfig getEvolutionConfig() throws ValidationException {
        EvolutionConfig evolutionConfig = new EvolutionConfig();
        evolutionConfig.setInitialPopulation(this.populationSize);
        Selection selection = new Selection(selectionType, (double) selectionPercentage,elitismSize);
        evolutionConfig.setSelection(selection);
        Crossover crossover = new Crossover();
        crossover.setName(crossoverType);
        crossover.setCuttingPoints(cuttingPoints);
        crossover.setConfiguration(orientationType);
        evolutionConfig.setCrossover(crossover);
        evolutionConfig.setMutationsList(this.mutations);
        return evolutionConfig;
    }
}
