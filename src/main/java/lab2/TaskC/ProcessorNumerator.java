package lab2.TaskC;

import java.util.ArrayList;

//Поток вычисляет максимальные расстояния для переданных точек в кластере
public class ProcessorNumerator implements Runnable {

    private ArrayList<double[]> dots;

    private ArrayList<double[]> cluster;

    private Result result;

    ProcessorNumerator(ArrayList<double[]> dots, ArrayList<double[]> cluster, Result accumulator) {
        this.dots = new ArrayList<>(dots);
        this.cluster = new ArrayList<>(cluster);
        this.result = accumulator;
    }

    @Override
    public void run() {
        for (double[] currentDot : dots) {
            double maxDistance = Double.MIN_VALUE;
            ArrayList<double[]> otherDots = new ArrayList<>(cluster);
            otherDots.remove(currentDot);
            for (double[] otherDot : otherDots) {
                double distance = Math.sqrt(Math.pow(currentDot[0] - otherDot[0], 2) + Math.pow(currentDot[1] - otherDot[1], 2));
                if (distance < maxDistance) {
                    maxDistance = distance;
                }
            }
            addMaxDistance(maxDistance);
        }
    }

    private synchronized void addMaxDistance(double maxDistance){
        result.numerator += maxDistance;
    }
}
