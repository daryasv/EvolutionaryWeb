package models;
import engine.models.*;
import exception.ValidationException;
import models.evolution.CrossoverConfigurationType;
import models.evolution.EvolutionConfig;
import models.evolution.Mutation;
import models.timeTable.*;
import schema.models.ETTDescriptor;

import java.io.Serializable;
import java.util.*;


public class TimeTableDataSet implements EvolutionDataSet<Lesson>, Serializable {

    final private TimeTableMembers timeTableMembers;
    private EvolutionConfig evolutionConfig;
    private int generations;
    private int generationsInterval;

    public TimeTableDataSet(ETTDescriptor descriptor, int generations, int generationsInterval) throws ValidationException {
        this.timeTableMembers = new TimeTableMembers(descriptor.getETTTimeTable());
        setGenerations(generations);
        setGenerationsInterval(generationsInterval);
    }

    public TimeTableDataSet(ETTDescriptor descriptor) throws ValidationException {
        this.timeTableMembers = new TimeTableMembers(descriptor.getETTTimeTable());
    }

    public TimeTableMembers getTimeTableMembers() {
        return timeTableMembers;
    }

    public EvolutionConfig getEvolutionConfig() {
        return evolutionConfig;
    }


    @Override
    public void mutation(Solution<Lesson> child, IMutation<Lesson> mutation) {
        if (mutation.getName().equals(Mutation.MutationOperators.FLIP_OPERATOR.getOperatorName())) {
                runFlippingMutation(child, mutation);
        }
        else if (mutation.getName().equals(Mutation.MutationOperators.FLIP_OPERATOR.getOperatorName())) {
            runSizerMutation(child, mutation);
        }
    }

    private void runSizerMutation(Solution<Lesson> child, IMutation<Lesson> mutation) {
        Random rand = new Random();
        boolean isPositive = mutation.getMaxTupples() >= 0;
        int randomTuplesNum = rand.nextInt(Math.abs(mutation.getMaxTupples()));
        int maxOptions = this.timeTableMembers.getDays() * this.timeTableMembers.getHours();
        int minOptions = this.timeTableMembers.getDays();

        for(int i=0; i<randomTuplesNum && child.getList().size() <maxOptions && child.getList().size() > minOptions ;i++) {
            if (isPositive) {
                Optional<Lesson> lesson = child.getList().stream().filter(p->p.getSubjectId() == -1).findFirst();
                int subjectIdx = rand.nextInt(this.timeTableMembers.getSubjects().size());
                int teacherIdx = rand.nextInt(this.timeTableMembers.getTeachers().size());
                lesson.ifPresent(value -> {
                    value.setSubjectId(this.timeTableMembers.getSubjects().get(subjectIdx).getId());
                    value.setSubjectId(this.timeTableMembers.getTeachers().get(teacherIdx).getId());
                });
            } else {
                Optional<Lesson> lesson = child.getList().stream().filter(p->p.getSubjectId() != -1).findFirst();
                lesson.ifPresent(value -> {
                    value.setSubjectId(-1);
                    value.setTeacherId(-1);
                });
            }
        }
    }

    private void runFlippingMutation(Solution <Lesson> child, IMutation mutation){
        Random rand = new Random();
        int randomTuplesNum = rand.nextInt(mutation.getMaxTupples());
        List<Integer> changed = new ArrayList<>();
        for(int i=0; i<randomTuplesNum ;) {
            int tupleIndex = rand.nextInt(child.getList().size());
            if(!changed.contains(tupleIndex)){
                changeComponent(child.getList().get(tupleIndex), mutation.getComponent());
                changed.add(tupleIndex);
                i++;
            }
        }
    }

    private void changeComponent(Lesson lesson, char component) {
        if (component == 'C') {
            int classCount = this.timeTableMembers.getGrades().size();
            Random rand = new Random();
            int randomIndex = rand.nextInt(classCount);
            int changedID = new ArrayList<>(this.timeTableMembers.getGrades().keySet()).get(randomIndex);
            lesson.setClassId(changedID);
        } else if (component == 'T') {
            int TeachersCount = this.timeTableMembers.getTeachers().size();
            Random rand = new Random();
            int randomIndex = rand.nextInt(TeachersCount);
            int changedID = new ArrayList<>(this.timeTableMembers.getTeachers().keySet()).get(randomIndex);
            lesson.setTeacherId(changedID);
        } else if (component == 'D') {
            int DaysCount = this.timeTableMembers.getDays();
            Random rand = new Random();
            int changedVal = rand.nextInt(DaysCount) + 1;
            lesson.setDay(changedVal);
        } else if (component == 'H') {
            int HoursCount = this.timeTableMembers.getHours();
            Random rand = new Random();
            int changedVal = rand.nextInt(HoursCount) + 1;
            lesson.setHour(changedVal);
        } else if (component == 'S') {
            int subjectsCount = this.timeTableMembers.getSubjects().size();
            Random rand = new Random();
            int randomIndex = rand.nextInt(subjectsCount + 1);
            if (randomIndex == subjectsCount) {
                lesson.setSubjectId(-1);
                lesson.setTeacherId(-1);
            } else {
                int changedVal = new ArrayList<>(this.timeTableMembers.getSubjects().keySet()).get(randomIndex);
                lesson.setSubjectId(changedVal);
            }
        }
    }

    @Override
    public int getPopulationSize() {
        return evolutionConfig.getInitialPopulation();
    }

    public void setGenerations(int generations) throws ValidationException {
        if(generations >= 100)
            this.generations = generations;
        else
            throw new ValidationException("generation smaller than 100");
    }

    @Override
    public int getGenerations() {
        return generations;
    }

    public void setGenerationsInterval(int generationsInterval) throws ValidationException {
        if (generationsInterval < 1) {
            throw new ValidationException("Generation interval must be bigger then 1");
        }
        this.generationsInterval = generationsInterval;
    }

    @Override
    public int getGenerationInterval() {
        return generationsInterval;
    }

    @Override
    public Solution<Lesson> getRandomSolution() {
        return timeTableMembers.generateRandomSolution();
    }

    @Override
    public int getHardRulesWeight() {
        return timeTableMembers.getHardRulesWeight();
    }

    @Override
    public List<IRule> getRules() {
        return new ArrayList<>(timeTableMembers.getRules());
    }

    @Override
    public ICrossoverData getCrossoverData()
    {
        return evolutionConfig.getCrossover();
    }

    @Override
    public double getFitness(Solution<Lesson> solution, IRule rule) {
        double fails = 0;
        List<Lesson> lessons = solution.getList();
        RuleId ruleId = RuleId.valueOf(rule.getName());
        switch (ruleId) {
            case TeacherIsHuman:
                HashMap<HourInDay, List<Integer>> teachersByHour = new HashMap<>();
                for (Lesson l : lessons) {
                    if (l.getTeacherId() != -1) {
                        HourInDay hourInDay = new HourInDay(l.getDay(), l.getHour());
                        if (teachersByHour.containsKey(hourInDay)) {
                            if (teachersByHour.get(hourInDay).contains(l.getTeacherId())) {
                                fails++;
                                continue;
                            }
                        } else {
                            teachersByHour.put(hourInDay, new ArrayList<>());
                        }
                        teachersByHour.get(hourInDay).add(l.getTeacherId());
                    }
                }
                return (1 - (fails / lessons.size())) * 100;
            case Singularity:
                HashMap<HourInDay, List<Integer>> classesByHour = new HashMap<>();
                for (Lesson l : lessons) {
                    HourInDay hourInDay = new HourInDay(l.getDay(), l.getHour());
                    if (classesByHour.containsKey(hourInDay)) {
                        if (classesByHour.get(hourInDay).contains(l.getClassId())) {
                            fails++;
                            continue;
                        }
                    } else {
                        classesByHour.put(hourInDay, new ArrayList<>());
                    }
                    classesByHour.get(hourInDay).add(l.getClassId());
                }
                return (1 - (fails / lessons.size())) * 100;

            case Knowledgeable:
                for (Lesson lesson : lessons) {
                    if (lesson.getSubjectId() != -1) {
                        Teacher teacher = timeTableMembers.getTeachers().get(lesson.getTeacherId());
                        if (teacher != null) {
                            if (!teacher.getSubjectsIdsList().contains(lesson.getSubjectId())) {
                                fails++;
                            }
                        } else {
                            fails++;
                        }
                    }
                }
                return (1 - (fails / lessons.size())) * 100;

            case Satisfactory:

                HashMap<Integer, HashMap<Integer, Integer>> hoursByClass = new HashMap<>();

                //set actual
                for (Lesson l : lessons) {
                    if (l.getSubjectId() == -1)
                        continue;
                    int grade = l.getClassId();
                    HashMap<Integer, Integer> subjectsCount;
                    if (!hoursByClass.containsKey(grade)) {
                        subjectsCount = new HashMap<>();
                        subjectsCount.put(l.getSubjectId(), 1);
                    } else {
                        subjectsCount = hoursByClass.get(grade);
                        int hours = 1;
                        if (subjectsCount.containsKey(l.getSubjectId())) {
                            hours += subjectsCount.get(l.getSubjectId());
                        }
                        subjectsCount.put(l.getSubjectId(), hours);
                    }

                    hoursByClass.put(l.getClassId(), subjectsCount);
                }

                int totalSubjectsExpect = 0;
                for (Grade grade : this.timeTableMembers.getGrades().values()) {
                    int gradeId = grade.getId();
                    if (hoursByClass.containsKey(gradeId)) {
                        //go over all subjects in grade
                        //check if they exist in actual
                        //mark all the subjects that are invalid hours
                        for (Integer subjectId : grade.getRequirements().keySet()) {
                            int expect = grade.getRequirements().get(subjectId);
                            int actual = 0;
                            if (hoursByClass.get(gradeId).containsKey(subjectId)) {
                                actual = hoursByClass.get(gradeId).get(subjectId);
                            }
                            if (expect != actual) {
                                fails++;
                            }
                        }

                        //check on actual if there is subjects that not relevant to the class
                        for (Integer subjectId : hoursByClass.get(gradeId).keySet()) {
                            if (!grade.getRequirements().containsKey(subjectId)) {
                                fails++;
                                totalSubjectsExpect++;
                            }
                        }
                    } else {
                        fails += grade.getRequirements().size();
                    }
                    totalSubjectsExpect += grade.getRequirements().size();
                }
                return (1 - (fails / totalSubjectsExpect)) * 100;

            case DayOffTeacher:

                List<Integer> days = new ArrayList<>();
                HashMap<Integer, List<Integer>> teacherDays = new HashMap<>();
                for (Lesson l : lessons) {
                    int id = l.getTeacherId();
                    int day = l.getDay();
                    if (!days.contains(day)) {
                        days.add(day);
                    }
                    if (!teacherDays.containsKey(l.getTeacherId())) {
                        teacherDays.put(id,new ArrayList<>());
                    }
                    if (!teacherDays.get(id).contains(day)) {
                        teacherDays.get(id).add(day);
                    }
                }
                int totalDays = days.size();
                for (int key: teacherDays.keySet()) {
                    if(teacherDays.get(key).size() == totalDays){
                        fails++;
                    }
                }
                return (1 - (fails / teacherDays.keySet().size())) * 100;

            case Sequentiality:

            case DayOffClass:

                List<Integer> days2 = new ArrayList<>();
                HashMap<Integer, List<Integer>> classDays = new HashMap<>();
                for (Lesson l : lessons) {
                    int id = l.getClassId();
                    int day = l.getDay();
                    if (!days2.contains(day)) {
                        days2.add(day);
                    }
                    if (!classDays.containsKey(l.getClassId())) {
                        classDays.put(id,new ArrayList<>());
                    }
                    if (!classDays.get(id).contains(day)) {
                        classDays.get(id).add(day);
                    }
                }
                int totalDays2 = days2.size();
                for (int key: classDays.keySet()) {
                    if(classDays.get(key).size() == totalDays2){
                        fails++;
                    }
                }
                return (1 - (fails / classDays.keySet().size())) * 100;

            case WorkingHoursPreference:

                HashMap<Integer, Integer> teachersHours = new HashMap<>();
                for (Lesson l : lessons) {
                    int id = l.getTeacherId();
                    if (!teachersHours.containsKey(id)) {
                        teachersHours.put(id,1);
                    }
                    else
                    {
                        teachersHours.put(id,teachersHours.get(id)+1);
                    }
                }
                for (int key: teachersHours.keySet()) {
                    if(teachersHours.get(key) != this.timeTableMembers.getTeachers().get(key).getWorkingHoursPreference()){
                        fails++;
                    }
                }
                return (1 - (fails / teachersHours.keySet().size())) * 100;

            default:
                break;
        }
        return 0;
    }

    public Solution<Lesson> sort(Solution<Lesson> solution, String operator,String configuration) {
        LessonSortType sortType = LessonSortType.valueOfLabel(operator);
        Solution<Lesson> sorted = new Solution<Lesson>();
        sorted.setList(new ArrayList<>(solution.getList())); //duplicate the solution
        sorted.getList().sort(new LessonComparator(sortType, CrossoverConfigurationType.valueOfLabel(configuration)));

        return sorted;
    }

    @Override
    public ISelectionData getSelectionData() {
        return getEvolutionConfig().getSelection();
    }

    @Override
    public List<IMutation<Lesson>> getMutations() {
        return new ArrayList<>(getEvolutionConfig().getMutations());
    }

    public void setEvolutionConfig(EvolutionConfig evolutionConfig){
        this.evolutionConfig = evolutionConfig;
    }
}
