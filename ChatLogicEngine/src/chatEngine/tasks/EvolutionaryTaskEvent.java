package chatEngine.tasks;

import javafx.event.Event;
import javafx.event.EventType;

public class EvolutionaryTaskEvent extends Event {

    public static final EventType<EvolutionaryTaskEvent> PAUSE = new EventType<>(ANY, "PAUSE");
    public static final EventType<EvolutionaryTaskEvent> STOP = new EventType<>(ANY, "STOP");
    public static final EventType<EvolutionaryTaskEvent> RESUME = new EventType<>(ANY, "RESUME");

    public EvolutionaryTaskEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

}
