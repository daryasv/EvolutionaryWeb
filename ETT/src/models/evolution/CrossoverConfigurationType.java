package models.evolution;

public enum CrossoverConfigurationType {
    CLASS("CLASS"), TEACHER("TEACHER");

    public String name;
    CrossoverConfigurationType(String name) {
        this.name = name;
    }

    public static CrossoverConfigurationType valueOfLabel(String label) {
        for (CrossoverConfigurationType e : values()) {
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
