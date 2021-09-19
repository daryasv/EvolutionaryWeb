package engine.models;

import java.io.Serializable;

public enum SelectionType implements Serializable {
    Truncation("Truncation"),
    RouletteWheel("RouletteWheel");

    public String name;
    SelectionType(String name) {
        this.name = name;
    }

    public static SelectionType valueOfLabel(String label) {
        for (SelectionType e : values()) {
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
