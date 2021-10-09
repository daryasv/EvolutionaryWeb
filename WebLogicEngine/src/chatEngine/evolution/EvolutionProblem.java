package chatEngine.evolution;

import chatEngine.tasks.RunEvolutionaryTask;
import models.TimeTableDataSet;
import models.evolution.EvolutionConfig;
import models.timeTable.Grade;
import models.timeTable.Rule;
import models.timeTable.Subject;
import models.timeTable.Teacher;

import java.util.*;

public class EvolutionProblem {
    int id;
    String name;
    String owner;
    TimeTableDataSet timeTable;
    Map<String, RunEvolutionaryTask> evolutionRuns;


    public EvolutionProblem(int id, String name, String owner) {
        evolutionRuns = new HashMap<>();
        this.id = id;
        this.owner = owner;
        this.name = name;
    }


    public TimeTableDataSet getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(TimeTableDataSet timeTable) {
        this.timeTable = timeTable;
    }

    public String getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public synchronized Map<String, RunEvolutionaryTask> getEvolutionRuns() {
        return Collections.unmodifiableMap(this.evolutionRuns);
    }


    public String getTimeTableSettings() {
        StringBuilder sbXmlEttSettings = new StringBuilder();

        HashMap<Integer, Subject> subjects = this.getTimeTable().getTimeTableMembers().getSubjects();
        HashMap<Integer, Teacher> teachers = this.getTimeTable().getTimeTableMembers().getTeachers();
        HashMap<Integer, Grade> grades = this.getTimeTable().getTimeTableMembers().getGrades();
        List<Rule> rules = this.getTimeTable().getTimeTableMembers().getRules();

        sbXmlEttSettings.append(toStringSubjects(subjects));
        sbXmlEttSettings.append("\n");
        sbXmlEttSettings.append(toStringTeachers(teachers, subjects));
        sbXmlEttSettings.append("\n");
        sbXmlEttSettings.append(toStringGrades(grades, subjects));
        sbXmlEttSettings.append("\n");
        sbXmlEttSettings.append(toStringRules(rules));

        return sbXmlEttSettings.toString();
    }

    private StringBuilder toStringSubjects(HashMap<Integer, Subject> subjects) {
        StringBuilder sbSubjects = new StringBuilder();
        sbSubjects.append("SUBJECTS\n");
        sbSubjects.append("__________________________________________________________\n");
        for (Map.Entry<Integer, Subject> entry : subjects.entrySet()) {
            sbSubjects.append(String.format("ID: %d  |  Name: %s\n", entry.getKey(), entry.getValue().getName()));
        }
        sbSubjects.append("__________________________________________________________\n");
        return sbSubjects;
    }

    private StringBuilder toStringTeachers(HashMap<Integer, Teacher> teachers, HashMap<Integer, Subject> subjects) {
        StringBuilder sbTeachers = new StringBuilder();
        sbTeachers.append("TEACHERS\n");
        sbTeachers.append("__________________________________________________________\n");
        for (Map.Entry<Integer, Teacher> entry : teachers.entrySet()) {
            sbTeachers.append(String.format("Teacher ID %d\n", entry.getKey()));
            sbTeachers.append(String.format("Teaching subjects:\n"));
            for (int i = 0; i < entry.getValue().getSubjectsIdsList().size(); i++) {
                int subjectID = entry.getValue().getSubjectsIdsList().get(i);
                sbTeachers.append(String.format("       Subject ID: %d  |  ", subjectID));
                sbTeachers.append(String.format("Name: %s\n", subjects.get(subjectID).getName()));
            }
            sbTeachers.append("\n");
        }
        sbTeachers.append("__________________________________________________________\n");
        sbTeachers.append("\n");

        return sbTeachers;
    }

    private StringBuilder toStringGrades(HashMap<Integer, Grade> grades, HashMap<Integer, Subject> subjects) {
        StringBuilder sbGrades = new StringBuilder();
        sbGrades.append("GRADES\n");
        sbGrades.append("__________________________________________________________\n");
        for (Map.Entry<Integer, Grade> entry : grades.entrySet()) {
            sbGrades.append(String.format("\nGrade ID %d\n", entry.getKey()));

            for (Map.Entry<Integer, Integer> required : entry.getValue().getRequirements().entrySet()) {
                sbGrades.append(String.format("Subject ID: %d  |  Name: %s  |  ", required.getKey(), subjects.get(required.getKey()).getName()));
                sbGrades.append(String.format("Required Hours: %d", required.getValue()));
                sbGrades.append("\n");
            }
            sbGrades.append("\n");
        }
        sbGrades.append("__________________________________________________________\n");
        sbGrades.append("\n");
        return sbGrades;
    }

    private StringBuilder toStringRules(List<Rule> rules) {
        StringBuilder sbRules = new StringBuilder();
        sbRules.append("RULES\n");
        sbRules.append("__________________________________________________________\n");
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).isHard())
                sbRules.append(String.format("Rule Name: %s  |  Type: Hard", rules.get(i).getName()));
            else {
                sbRules.append(String.format("Rule Name: %s  |  Type: Soft", rules.get(i).getName()));
            }
            sbRules.append("\n");
        }
        sbRules.append("__________________________________________________________\n");
        sbRules.append("\n");
        return sbRules;
    }

    public synchronized void runEvolution(String username, EvolutionConfig evolutionConfig) {
        if(this.evolutionRuns.containsKey(username)){
            this.evolutionRuns.get(username).setEvolutionConfig(evolutionConfig);
            new Thread(evolutionRuns.get(username)).start();
        }else {
            RunEvolutionaryTask runEvolutionaryTask = new RunEvolutionaryTask(timeTable,evolutionConfig, evolutionConfig.getEndCondition().toString(),evolutionConfig.getLimit(),evolutionConfig.getInterval());
            new Thread(runEvolutionaryTask).start();
            evolutionRuns.put(username, runEvolutionaryTask);
        }
    }
}



