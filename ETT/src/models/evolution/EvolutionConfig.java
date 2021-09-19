package models.evolution;

import exception.ValidationException;
import schema.models.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EvolutionConfig implements Serializable
{
    private int initialPopulation;
    private Selection selection;
    private Crossover crossover;
    private List<Mutation> mutations;

    public EvolutionConfig(ETTEvolutionEngine ettEvolutionEngine) throws ValidationException {
        setInitialPopulation(ettEvolutionEngine.getETTInitialPopulation());
        setCrossover(ettEvolutionEngine.getETTCrossover());
        setSelection(ettEvolutionEngine.getETTSelection());
        setMutations(ettEvolutionEngine.getETTMutations().getETTMutation());
    }

    public int getInitialPopulation() {
        return initialPopulation;
    }


    public void setInitialPopulation(ETTInitialPopulation ettInitialPopulation) throws ValidationException {
        setInitialPopulation(ettInitialPopulation.getSize());
    }

    public void setInitialPopulation(int initialPopulation) throws ValidationException {
        if(initialPopulation <= 0) {
            throw new ValidationException("Invalid initial population");
        }
        this.initialPopulation = initialPopulation;
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(ETTSelection ettSelection) throws ValidationException {
        this.selection = new Selection(ettSelection);
        if(selection.getElitismCount() > initialPopulation){
            throw new ValidationException("Elitism can't be more then initial population");
        }
    }

    public void setSelection(Selection selection) throws ValidationException {
        this.selection = selection;
        if(selection.getElitismCount() > initialPopulation){
            throw new ValidationException("Elitism can't be more then initial population");
        }
    }

    public Crossover getCrossover() {
        return crossover;
    }

    public void setCrossover(ETTCrossover ettCrossover) throws ValidationException {
        this.crossover = new Crossover(ettCrossover);
    }

    public void setCrossover(Crossover crossover){
        this.crossover = crossover;
    }

    public List<Mutation> getMutations() {
        return mutations;
    }

    public void setMutations(List<ETTMutation> ettMutations) throws ValidationException {
        this.mutations = new ArrayList<>();
        for (ETTMutation ettMutation: ettMutations) {
            mutations.add(new Mutation(ettMutation));
        }
    }

    public void setMutationsList(List<Mutation> mutations){
        this.mutations = mutations;
    }
}
