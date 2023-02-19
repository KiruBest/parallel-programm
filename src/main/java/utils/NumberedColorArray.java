package utils;

import java.awt.*;
import java.util.Comparator;

public class NumberedColorArray {
    private Color[][] colorArray;
    private int position;

    public NumberedColorArray(Color[][] colorArray, int position) {
        this.colorArray = colorArray;
        this.position = position;
    }

    public Color[][] getColorArray() {
        return colorArray;
    }

    public void setColorArray(Color[][] colorArray) {
        this.colorArray = colorArray;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static class NumberedColorComparator implements Comparator<NumberedColorArray> {

        @Override
        public int compare(NumberedColorArray o1, NumberedColorArray o2) {
            int firstPosition = o1.getPosition();
            int secondPosition = o2.getPosition();
            return Integer.compare(firstPosition, secondPosition);
        }
    }
}
