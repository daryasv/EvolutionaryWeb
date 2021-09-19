package engine.models;

import java.util.List;

public class SelectionResult<T> {
    List<SolutionFitness<T>> eliteSolutions;
    List<SolutionFitness<T>> selectionSolutions;

    public SelectionResult(List<SolutionFitness<T>> eliteSolutions, List<SolutionFitness<T>> selectionSolutions) {
        this.eliteSolutions = eliteSolutions;
        this.selectionSolutions = selectionSolutions;
    }

    public List<SolutionFitness<T>> getEliteSolutions() {
        return eliteSolutions;
    }

    public List<SolutionFitness<T>> getSelectionSolutions() {
        return selectionSolutions;
    }
}
