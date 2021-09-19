package engine.models;

public interface ISelectionData {
    SelectionType getType();

    int getValue();

    int getElitismCount();
}
