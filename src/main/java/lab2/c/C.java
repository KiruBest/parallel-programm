package lab2.c;

import com.opencsv.exceptions.CsvException;
import ui.ClusterVisualization;
import utils.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class C {
    private static final String Column_Temp_pvariance = "Temp_pvariance";
    private static final String Column_HCO3_pvariance = "HCO3_pvariance";

    public static void main(String[] args) {

        try {
//            Все количества кластеров и потоков для выполнения за один билд
            List<Integer> kList = new ArrayList<>(List.of(5));
            List<Integer> threadNumbers = new ArrayList<>(List.of(2));

            ArrayList<ArrayList<double[]>> clusters;

//            Провайдер предоставляет датасет в формате, принимаемом классом K-средних
            ArrayList<double[]> dataSet = DataSetProvider.getDataSet(Column_Temp_pvariance, Column_HCO3_pvariance);

//            За один билд получаем время выполнения всех случаев: для всех k и для всех количеств потоков
            for (int numberOfClusters : kList) {
                for (int threadNumber : threadNumbers) {

                    long time = System.currentTimeMillis();

                    KMeans kmeans = new KMeans(numberOfClusters);
                    kmeans.setDataSet(dataSet);
                    kmeans.execute();
//                    Получаем готовые кластеры
                    clusters = kmeans.getCluster();

//                    Считаем числитель синхронно
                    ProcessorDenominator processorDenominator = new ProcessorDenominator(kmeans.center);
                    processorDenominator.run();
                    ClusterRequest clusterRequest = new ClusterRequest();
                    clusterRequest.denominator = processorDenominator.minDistanceSum;

//                    Определяем, сколько точек обрабатывает один поток
                    int numberOfDotsPerThread = DataSetProvider.listX.size() / threadNumber;
                    if (DataSetProvider.listX.size() % threadNumber > 0) {
                        numberOfDotsPerThread++;
                    }
                    ArrayList<double[]> dots = new ArrayList<>();
                    ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
//                    Собираем точки в группы по numberOfDotsPerThread и запускаем поток, обрабатывающий эти точки
                    for (int j = 0; j < numberOfClusters; j++) {
                        for (int k = 0; k < clusters.get(j).size(); k++) {
                            dots.add(clusters.get(j).get(k));
                            if (dots.size() == numberOfDotsPerThread) {
                                executor.execute(new ProcessorNumerator(dots, clusters.get(j), clusterRequest));
                                dots.clear();
                            }
                        }
                    }
//                    Возможно остались точки, которые мы не обработали, обработаем их тут
                    if (dots.size() != 0) {
                        executor.execute(new ProcessorNumerator(dots, clusters.get(clusters.size() - 1), clusterRequest));
                        dots.clear();
                    }

//                    Ждем завершения потоков и подводим итоги
                    boolean isSuccess = ThreadExtensions.awaitAllThreads(executor);
                    if (isSuccess) {
                        System.out.println("Время обработки = " + (System.currentTimeMillis() - time));
                        System.out.println("Оценка точности: " + clusterRequest.getResult());
                        for (double[] center : kmeans.center) {
                            System.out.println("Центр кластера [" + kmeans.center.indexOf(center) + "] " + Arrays.toString(center));
                        }

//                        Смотрим график
                        ArrayList<Double> cordX = new ArrayList<>();
                        ArrayList<Double> cordY = new ArrayList<>();
                        ArrayList<Color> colors = new ArrayList<>();
                        ArrayList<Color> colorChoice = new ArrayList<>(List.of(Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.BLACK));
                        for (ArrayList<double[]> cluster : clusters) {
                            for (double[] dot : cluster) {
                                cordX.add(dot[0]);
                                cordY.add(dot[1]);
                                colors.add(colorChoice.get(clusters.indexOf(cluster)));
                            }
                        }
                        ClusterVisualization clusterVisualization = new ClusterVisualization(cordX, cordY, colors);
                        clusterVisualization.draw(cordX, cordY, colors);
                    }
                }
            }

        } catch (IOException | CsvException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
