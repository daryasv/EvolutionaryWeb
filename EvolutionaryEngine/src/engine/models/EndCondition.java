package engine.models;

import java.io.Serializable;


public interface EndCondition {
    enum EndConditionType implements Serializable {
        Generations("Generations"),
        Fitness("Fitness"),
        Time("Time");

        public String name;
        EndConditionType(String name) {
            this.name = name;
        }

        public static EndConditionType valueOfLabel(String label) {
            for (EndConditionType e : values()) {
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

    EndConditionType getEndCondition();

    double getLimit();
}
