package com.mak.classportal.utilities;

public class TestTime {
    public int seconds;
    public int minutes;
    public int hours;

    public TestTime(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;

    }

    public static TestTime difference(TestTime start, TestTime stop) {
        TestTime diff = new TestTime(0, 0, 0);
        if (stop.seconds > start.seconds) {
            --start.minutes;
            start.seconds += 60;
        }
        diff.seconds = stop.seconds - start.seconds;
        if (stop.minutes > start.minutes) {
            --start.hours;
            start.minutes += 60;
        }
        diff.minutes = stop.minutes - start.minutes;
        diff.hours = stop.hours - start.hours;
        return (diff);
    }
}