package engine.models;

import java.util.List;

public interface EvolutionDataSet<T> {

    int getPopulationSize();

    int getGenerations();

    int getGenerationInterval();

    Solution<T> getRandomSolution();

    int getHardRulesWeight();

    List<IRule> getRules();

    ICrossoverData getCrossoverData();

    double getFitness(Solution<T> solution, IRule rule);

    void mutation(Solution <T> child,IMutation<T> mutation);

    Solution<T> sort(Solution<T> solution, String operator,String configuration);

    ISelectionData getSelectionData();

    List<IMutation<T>> getMutations();

}
