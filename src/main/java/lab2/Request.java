package lab2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class Request {
    private final ArrayList<NumberedColorArray> numberedColorArrayList = new ArrayList<>();

    public Color[][] getColorArray() {
        Color[][] result = new Color[0][0];
        numberedColorArrayList.sort(new NumberedColorArray.NumberedColorComparator());
        for (NumberedColorArray numberedColorArray : numberedColorArrayList) {
            result = Stream.concat(
                    Arrays.stream(result),
                    Arrays.stream(numberedColorArray.getColorArray())
            ).toArray(Color[][]::new);
        }
        return result;
    }

    public synchronized void addColorArray(NumberedColorArray numberedColorArray) {
        numberedColorArrayList.add(numberedColorArray);
    }
}
