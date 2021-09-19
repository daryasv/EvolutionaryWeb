package engine.models;

public interface IMutation<T> {
    double getProbability();

    int getMaxTupples();

    char getComponent();

    String getName();
}
