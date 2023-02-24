package lab2.c;

import java.util.ArrayList;

//Вынесенный процессор для вычисления суммы наименьших расстояний между центрами
public class ProcessorDenominator implements Runnable {

    double minDistanceSum;

    private final ArrayList<double[]> centers;

    ProcessorDenominator(ArrayList<double[]> centers) {
        this.centers = centers;
    }

    @Override
    public void run() {
        minDistanceSum = 0;
        for (int i = 0; i < centers.size(); i++) {
            double minDistance = Double.MAX_VALUE;
            double[] currentCenter = centers.get(i);
            ArrayList<double[]> otherCenters = new ArrayList<>(centers);
            otherCenters.remove(i);
            for (double[] otherCenter : otherCenters) {
                double distance = Math.sqrt(Math.pow(currentCenter[0] - otherCenter[0], 2) + Math.pow(currentCenter[1] - otherCenter[1], 2));
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
            minDistanceSum += minDistance;
        }
    }
}
