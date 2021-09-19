package models;

import models.evolution.CrossoverConfigurationType;

import java.util.Comparator;

public class LessonComparator implements Comparator<Lesson> {

    LessonSortType sortType;
    CrossoverConfigurationType configurationType;
    public LessonComparator(LessonSortType sortType,CrossoverConfigurationType configurationType) {
        this.sortType = sortType;
        this.configurationType = configurationType;
    }

    @Override
    public int compare(Lesson lesson1, Lesson lesson2) {
        int compare = 0;
        switch (sortType) {
            case DayTimeOriented:
                compare = lesson1.getDay() - lesson2.getDay();
                if (compare == 0) {
                    compare = lesson1.getHour() - lesson2.getHour();
                    if (compare == 0) {
                        compare = lesson1.getClassId() - lesson2.getClassId();
                        if (compare == 0) {
                            compare = lesson1.getTeacherId() - lesson2.getTeacherId();
                        }
                    }
                }
                break;

            case AspectOriented:
                if (configurationType == CrossoverConfigurationType.CLASS) {
                    compare = lesson1.getClassId() - lesson2.getClassId();
                } else if (configurationType == CrossoverConfigurationType.TEACHER) {
                    compare = lesson1.getTeacherId() - lesson2.getTeacherId();
                }
                break;
            default:
                break;
        }

        return compare;
    }
}
