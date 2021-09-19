package models;

import java.io.Serializable;

public class Lesson implements Serializable {
    private int classId;
    private int teacherId;
    private int subjectId;
    private int day;
    private int hour;

    public Lesson(int classId, int teacherId, int subjectId, int day, int hour) {
        this.classId = classId;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.day = day;
        this.hour = hour;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}
