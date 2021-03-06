package chatEngine.evolution;

import models.TimeTableDataSet;
import models.evolution.EvolutionConfig;
import schema.models.ETTTimeTable;

import java.util.*;

public class EvolutionManager {
    private int lastProblemId = 0;
    private final Map<Integer,EvolutionProblem> evolutionProblemsMap;

    public EvolutionManager() {
        evolutionProblemsMap = new HashMap<>();
    }

    public synchronized void addEvolutionProblem(String name,String username, TimeTableDataSet timeTable) {
        EvolutionProblem evolutionProblem = new EvolutionProblem(lastProblemId,name,username);
        evolutionProblem.setTimeTable(timeTable);
        evolutionProblemsMap.put(lastProblemId,evolutionProblem);
        lastProblemId ++;
    }

//    public synchronized void removeEvolutionProblem(EvolutionProblem username) {
//        evolutionProblemsSet.remove(EvolutionProblem);
//    }

    public synchronized Map<Integer,EvolutionProblem> getEvolutionProblemsMap() {
        return Collections.unmodifiableMap(evolutionProblemsMap);
    }

    public synchronized void runEvolution(int id, String username, EvolutionConfig evolutionConfig) {
        evolutionProblemsMap.get(id).runEvolution(username, evolutionConfig);
    }

    public synchronized int getVersion() {
        return lastProblemId;
    }

    public synchronized void pauseEvolution(int id, String username) {
        evolutionProblemsMap.get(id).getEvolutionRuns().get(username).pause();
    }

    public synchronized void stopEvolution(int id, String username) {
        evolutionProblemsMap.get(id).getEvolutionRuns().get(username).stop();
    }
}
