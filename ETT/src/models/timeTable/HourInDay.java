package models.timeTable;

import java.util.Objects;

public class HourInDay {
    int day;
    int hour;

    public HourInDay(int day, int hour) {
        this.day = day;
        this.hour = hour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HourInDay hourInDay = (HourInDay) o;
        return day == hourInDay.day && hour == hourInDay.hour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, hour);
    }
}
