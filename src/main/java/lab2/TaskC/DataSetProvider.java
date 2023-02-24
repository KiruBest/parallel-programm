package lab2.TaskC;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import constants.Path;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataSetProvider {

    public static List<Double> listX = new ArrayList<>();
    public static List<Double> listY = new ArrayList<>();

    public static ArrayList<double[]> getDataSet(String strColumnX, String strColumnY) throws IOException, CsvException {
        FileReader fileReader = new FileReader(Path.CSV_PATH);
        CSVReader csvReader = new CSVReader(fileReader);
//        С помощью библиотеки получаем строки файла, однако можно было сделать это и в ручную
        List<String[]> list = csvReader.readAll();

//        Запоминаем какие колонки нам нужны
        int columnX = 0;
        int columnY = 0;
        String[] columnNames = list.get(0);
        for (int i = 0; i < columnNames.length; i++) {
            if (Objects.equals(columnNames[i], strColumnX)) {
                columnX = i;
            }
            if (Objects.equals(columnNames[i], strColumnY)) {
                columnY = i;
            }
        }


//        Находим наибольшие значения для нормирования
        double maxX = 0.0;
        double maxY = 0.0;

        for (int i = 1; i < list.size(); i++) {
            String[] column = list.get(i);
            String strX = column[columnX];
            String strY = column[columnY];
            if (!strX.isBlank() && !strY.isBlank() && !strX.equals("0.0") && !strY.equals("0.0")) {
                double x = Double.parseDouble(strX);
                double y = Double.parseDouble(strY);

                if (x > maxX) {
                    maxX = x;
                }
                if (y > maxY) {
                    maxY = y;
                }

                listX.add(Double.valueOf(strX));
                listY.add(Double.valueOf(strY));
            }
        }

//        Нормируем
        for (int i = 0; i < listX.size(); i++){
            listX.set(i, listX.get(i) / maxX);
            listY.set(i, listY.get(i) / maxY);
        }

        ArrayList<double[]> dataSet = new ArrayList<>();

//        Формируем приемлимы вид датасета
        for (int i = 0; i < listX.size(); i++) {
            dataSet.add(new double[]{listX.get(i), listY.get(i)});
        }

        return dataSet;
    }
}
