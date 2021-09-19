package models.timeTable;

public enum RuleId {
    TeacherIsHuman("TeacherIsHuman"),
    Singularity("Singularity"),
    Knowledgeable("Knowledgeable"),
    Satisfactory("Satisfactory"),
    Sequentiality("Sequentiality"),
    DayOffTeacher("DayOffTeacher");

    public String name;
    RuleId(String name) {
        this.name = name;
    }

    public static RuleId valueOfLabel(String label) {
        for (RuleId e : values()) {
            if (e.name.equals(label)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
