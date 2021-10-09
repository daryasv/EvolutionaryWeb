package chat.models;

import java.util.List;

public class EvolutionProblems {
    int version;
    List<EvolutionProblemItem> items;

    public EvolutionProblems(int version, List<EvolutionProblemItem> items){
        this.version = version;
        this.items = items;
    }

}
