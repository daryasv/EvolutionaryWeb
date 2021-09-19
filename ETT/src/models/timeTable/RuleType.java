package models.timeTable;

public enum RuleType
{
    SOFT("Soft"), HARD("Hard");

    public String name;
    RuleType(String name) {
        this.name = name;
    }

    public static RuleType valueOfLabel(String label) {
        for (RuleType e : values()) {
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
