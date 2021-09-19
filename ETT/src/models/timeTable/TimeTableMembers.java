package models.timeTable;

import engine.models.Solution;
import exception.ValidationException;
import models.Lesson;
import schema.models.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TimeTableMembers implements Serializable
{
    private int days;
    private int hours;
    private HashMap<Integer,Teacher> teachers;
    private HashMap<Integer,Subject> subjects;
    private HashMap<Integer,Grade> grades;
    private List<Rule> rules;
    private int hardRulesWeight;

    public TimeTableMembers(ETTTimeTable timeTableMembers) throws ValidationException {
        setDays(timeTableMembers.getDays());
        setHours(timeTableMembers.getHours());
        setSubjects(timeTableMembers.getETTSubjects().getETTSubject());
        setGrades(timeTableMembers.getETTClasses().getETTClass());
        setTeachers(timeTableMembers.getETTTeachers().getETTTeacher());
        setRules(timeTableMembers.getETTRules().getETTRule());
        setHardRulesWeight(timeTableMembers.getETTRules().getHardRulesWeight());
    }

    public int getHardRulesWeight() {
        return hardRulesWeight;
    }

    public void setHardRulesWeight(int hardRulesWeight) {
        this.hardRulesWeight = hardRulesWeight;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) throws ValidationException {
        if (days < 1 || days > 7) {
            throw new ValidationException("Invalid days num: " + days);
        }
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) throws ValidationException {
        if (days < 1 || hours > 24) {
            throw new ValidationException("Invalid hours num: " + hours);
        }
        this.hours = hours;
    }

    public HashMap<Integer,Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<ETTTeacher> ettTeachers) throws ValidationException {
        this.teachers = new HashMap<>();
        List<Integer> subjectIds = new ArrayList<>(this.subjects.keySet());

        for (ETTTeacher ettTeacher : ettTeachers) {

            Teacher teacher = new Teacher(ettTeacher);
            if (teacher.getSubjectsIdsList().size() == 0) {
                throw new ValidationException("Teacher not teaching any subject");
            }
            for (Integer id : teacher.getSubjectsIdsList()) {
                if (!subjectIds.contains(id)) {
                    throw new ValidationException("Teacher subject id not in subjects");
                }
            }
            this.teachers.put(teacher.getId(), teacher);
        }

        List<Component> components = new ArrayList<>(this.teachers.values());
        if (!Component.checkIfRunningIds(components))
            throw new ValidationException("Teachers ids are not in running order");
    }

    public HashMap<Integer,Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<ETTSubject> ettSubjects) throws ValidationException {
        this.subjects = new HashMap<>();
        if(ettSubjects.size() == 0){
            throw new ValidationException("Subjects list cant be empty");
        }
        for (ETTSubject ettSubject : ettSubjects) {
            Subject subject = new Subject(ettSubject);
            this.subjects.put(subject.getId(),subject);
        }
        List<Component> components = new ArrayList<>(this.subjects.values());
        if (!Component.checkIfRunningIds(components)) {
            throw new ValidationException("Subjects ids are not in running order");
        }
    }

    public HashMap<Integer,Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<ETTClass> ettClasses) throws ValidationException {
        this.grades = new HashMap<>();
        List<Integer> subjectIds = new ArrayList<>(this.subjects.keySet());

        for (ETTClass ettClass : ettClasses) {
            Grade grade = new Grade(ettClass);
            if(grade.getRequirements().size() == 0){
                throw new ValidationException("Grade requirements cant be empty");
            }
            for (Integer id : grade.getRequirements().keySet()){
                if(!subjectIds.contains(id)){
                    throw new ValidationException("Teacher subject id not in subjects");
                }
            }
            int sumOfHours = grade.getRequirements().values().stream().mapToInt(Integer::intValue).sum();
            if(sumOfHours > hours * days){
                throw new ValidationException("Class subjects hours cant be more then week hours");
            }
            this.grades.put(grade.getId(), grade);
        }

        List<Component> grades = new ArrayList<>(this.grades.values());
        if (!Component.checkIfRunningIds(grades))
            throw new ValidationException("Grades ids are not in running order");
    }

    public List<Rule> getRules() {
        return rules;
    }

    private void setRules(List<ETTRule> ettRules) throws ValidationException {
        this.rules = new ArrayList<Rule>();
        for (ETTRule ettRule : ettRules) {
            Rule rule = new Rule(ettRule);
            if(rules.contains(rule)){
                throw new ValidationException("Rule " +rule.getId()+ " already exists");
            }
            this.rules.add(rule);
        }
    }

    public Solution<Lesson> generateRandomSolution() {

        Solution<Lesson> solution = new Solution<>();

        List<Integer> teachersIds = new ArrayList<>(this.teachers.keySet());
        List<Integer> subjectsIds = new ArrayList<>(this.subjects.keySet());;
        subjectsIds.add(-1); //add option of empty lesson

        Random rand = new Random();
        grades.keySet().forEach(grade -> {
            for (int d = 1; d <= days; d++) {
                for (int h = 1; h <= hours; h++) {
                    int randomSubject = subjectsIds.get(rand.nextInt(subjectsIds.size()));
                    int randomTeacher = randomSubject == -1 ? -1 : teachersIds.get(rand.nextInt(teachersIds.size()));

                    Lesson lesson = new Lesson(grade, randomTeacher, randomSubject, d, h);
                    solution.addItemToList(lesson);
                }
            }
        });
        return solution;
    }
}
