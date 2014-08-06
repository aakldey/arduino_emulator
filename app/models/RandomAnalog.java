package models;

public class RandomAnalog {

    public RandomAnalog(int pin, int start, int end) {
        this.pinNumber = pin;
        this.rangeStart = start;
        this.rangeEnd = end;
    }

    private int pinNumber;

    private int rangeStart = 0;

    private int rangeEnd = 1023;

    public int getPinNumber() {
        return pinNumber;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }
}
