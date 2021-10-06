package engine.models;

public interface ISelectionData {
    SelectionType getType();

    double getConfiguration();

    int getElitismCount();
}
