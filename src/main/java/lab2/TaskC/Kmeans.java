package lab2.TaskC;


import java.util.ArrayList;
import java.util.Random;

/**
 * Алгоритм кластеризации K-средних
 */
public class Kmeans {
    private int k; // На сколько кластеров он делится
    private int m; // Количество итераций
    private int dataSetLength; // Количество элементов набора данных, то есть длина набора данных
    private ArrayList<double[]> dataSet; // Список наборов данных
    public ArrayList<double[]> center; // Центр связанного списка
    private ArrayList<ArrayList<double[]>> cluster; // cluster
    private ArrayList<Double> jc; // Сумма квадратов ошибок, чем ближе k к dataSetLength, тем меньше ошибка
    private Random random;

    /**
     * Установите исходный набор данных для группировки
     *
     * @param dataSet
     */

    public void setDataSet(ArrayList<double[]> dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Получить группу результатов
     *
     * @return набор результатов
     */

    public ArrayList<ArrayList<double[]>> getCluster() {
        return cluster;
    }

    /**
     * Конструктор, передавая количество кластеров, которые будут разделены
     *
     * @param k Количество кластеров, если k <= 0, установить на 1, если k больше, чем длина источника данных, установить на длину источника данных
     */
    public Kmeans(int k) {
        if (k <= 0) {
            k = 1;
        }
        this.k = k;
    }

    /**
     * Инициализация
     */
    private void init() {
        m = 0;
        random = new Random();
        if (dataSet == null || dataSet.size() == 0) {
            initDataSet();
        }
        dataSetLength = dataSet.size();
        if (k > dataSetLength) {
            k = dataSetLength;
        }
        center = initCenters();
        cluster = initCluster();
        jc = new ArrayList<Double>();
    }

    /**
     * Если вызывающая сторона не инициализирует набор данных, используется набор внутренних тестовых данных
     */
    private void initDataSet() {
        dataSet = new ArrayList<double[]>();
        // где {6,3} то же самое, поэтому набор данных длиной 15 делится на 14 кластеров, а ошибка 15 кластеров равна 0
        double[][] dataSetArray = new double[][]{{8, 2}, {3, 4}, {2, 5},
                {4, 2}, {7, 3}, {6, 2}, {4, 7}, {6, 3}, {5, 3},
                {6, 3}, {6, 9}, {1, 6}, {3, 9}, {4, 1}, {8, 6}};

        for (int i = 0; i < dataSetArray.length; i++) {
            dataSet.add(dataSetArray[i]);
        }
    }

    /**
     * Инициализируйте центральный список данных, столько точек кластера, сколько кластеров
     *
     * @ возвращение центральной точки
     */
    private ArrayList<double[]> initCenters() {
        ArrayList<double[]> center = new ArrayList<double[]>();
        int[] randoms = new int[k];
        boolean flag;
        int temp = random.nextInt(dataSetLength);
        randoms[0] = temp;
        for (int i = 1; i < k; i++) {
            flag = true;
            while (flag) {
                temp = random.nextInt(dataSetLength);
                int j = 0;
                // Не ясно, что цикл for приводит к тому, что j не увеличивается на 1.
                // for(j=0;j<i;++j)
                // {
                // if(temp==randoms[j]);
                // {
                // break;
                // }
                // }
                while (j < i) {
                    if (temp == randoms[j]) {
                        break;
                    }
                    j++;
                }
                if (j == i) {
                    flag = false;
                }
            }
            randoms[i] = temp;
        }

        // Тестирование генерации случайных чисел
        // for(int i=0;i<k;i++)
        // {
        // System.out.println("test1:randoms["+i+"]="+randoms[i]);
        // }

        // System.out.println();
        for (int i = 0; i < k; i++) {
            center.add(dataSet.get(randoms[i])); // Генерировать связанный список центра инициализации
        }
        return center;
    }

    /**
     * Инициализировать коллекцию кластеров
     *
     * @return Кластер пустых данных, разделенный на k кластеров
     */
    private ArrayList<ArrayList<double[]>> initCluster() {
        ArrayList<ArrayList<double[]>> cluster = new ArrayList<ArrayList<double[]>>();
        for (int i = 0; i < k; i++) {
            cluster.add(new ArrayList<double[]>());
        }

        return cluster;
    }

    /**
     * Рассчитать расстояние между двумя точками
     *
     * @param element Пункт 1
     * @param center  Пункт 2
     * @ расстояние возврата
     */
    private double distance(double[] element, double[] center) {
        double distance = 0.0f;
        double x = element[0] - center[0];
        double y = element[1] - center[1];
        double z = x * x + y * y;
        distance = (double) Math.sqrt(z);

        return distance;
    }

    /**
     * Получить положение наименьшего расстояния от набора
     *
     * @param distance Массив расстояний
     * @return Положение минимального расстояния в массиве расстояний
     */
    private int minDistance(double[] distance) {
        double minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] < minDistance) {
                minDistance = distance[i];
                minLocation = i;
            } else if (distance[i] == minDistance) // Если они равны, вернуть случайную позицию
            {
                if (random.nextInt(10) < 5) {
                    minLocation = i;
                }
            }
        }

        return minLocation;
    }

    /**
     * Core, поместите текущий элемент в кластер, связанный с минимальным расстоянием центра
     */
    private void clusterSet() {
        double[] distance = new double[k];
        for (int i = 0; i < dataSetLength; i++) {
            for (int j = 0; j < k; j++) {
                distance[j] = distance(dataSet.get(i), center.get(j));
                // System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);

            }
            int minLocation = minDistance(distance);
            // System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
            // System.out.println();

            cluster.get(minLocation).add(dataSet.get(i)); // Ядро, поместите текущий элемент в кластер, связанный с центром минимального расстояния

        }
    }

    /**
     * Метод нахождения квадрата двухточечной ошибки
     *
     * @param element Пункт 1
     * @param center  Пункт 2
     * @ return ошибка в квадрате
     */
    private double errorSquare(double[] element, double[] center) {
        double x = element[0] - center[0];
        double y = element[1] - center[1];

        double errSquare = x * x + y * y;

        return errSquare;
    }

    /**
     * Рассчитать сумму метода квадратичной ошибки
     */
    private void countRule() {
        double jcF = 0;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                jcF += errorSquare(cluster.get(i).get(j), center.get(i));

            }
        }
        jc.add(jcF);
    }

    /**
     * Установить новый метод кластерного центра
     */
    private void setNewCenter() {
        for (int i = 0; i < k; i++) {
            int n = cluster.get(i).size();
            if (n != 0) {
                double[] newCenter = {0, 0};
                for (int j = 0; j < n; j++) {
                    newCenter[0] += cluster.get(i).get(j)[0];
                    newCenter[1] += cluster.get(i).get(j)[1];
                }
                // Установить среднее
                newCenter[0] = newCenter[0] / n;
                newCenter[1] = newCenter[1] / n;
                center.set(i, newCenter);
            }
        }
    }

    /**
     * Печать данных для тестирования
     *
     * @param dataArray     набор данных
     * @param dataArrayName Имя набора данных
     */
    public void printDataArray(ArrayList<double[]> dataArray,
                               String dataArrayName) {
        for (int i = 0; i < dataArray.size(); i++) {
            System.out.println("print:" + dataArrayName + "[" + i + "]={"
                    + dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}");
        }
        System.out.println("===================================");
    }

    /**
     * Основной метод процесса алгоритма Kmeans
     */
    private void kmeans() {
        init();
        // printDataArray(dataSet,"initDataSet");
        // printDataArray(center,"initCenter");

        // Циклическая группировка до постоянной ошибки
        while (true) {
            clusterSet();
            // for(int i=0;i<cluster.size();i++)
            // {
            // printDataArray(cluster.get(i),"cluster["+i+"]");
            // }

            countRule();

            // System.out.println("count:"+"jc["+m+"]="+jc.get(m));

            // System.out.println();
            // Ошибка не меняется, группировка завершена
            if (m != 0) {
                if (jc.get(m) - jc.get(m - 1) == 0) {
                    break;
                }
            }

            setNewCenter();
            // printDataArray(center,"newCenter");
            m++;
            cluster.clear();
            cluster = initCluster();
        }

        // System.out.println ("note: времена повтора: m =" + m); // Вывод времени итерации
    }

    /**
     * Алгоритм выполнения
     */
    public void execute() {
        long startTime = System.currentTimeMillis();
        System.out.println("kmeans begins");
        kmeans();
        long endTime = System.currentTimeMillis();
        System.out.println("kmeans running time=" + (endTime - startTime)
                + "ms");
        System.out.println("kmeans ends");
        System.out.println();
    }
}

