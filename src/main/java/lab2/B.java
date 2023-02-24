package lab2;

import utils.ImageUtils;
import utils.ProcessorRunnableCallback;
import utils.Request;
import utils.ThreadExtensions;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import static constants.Path.*;

public class B {
    private static final int THREAD_COUNT = 16;

    //    Собираем все файлики и для каждого выполняем вычисления - за один билд
    public static void main(String[] args) {
        try {
            File imgFolder = new File(IMG_PATH);
            File[] listOfFiles = Objects.requireNonNull(imgFolder.listFiles());

            for (int i = 1; i <= 3; i++) {
                System.out.println("#" + i + "\n");

                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        execute(fileName);
                    }
                }
            }
        } catch (IOException | NullPointerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void execute(String fileName) throws IOException, InterruptedException {
        System.out.println("Обработка файла: " + fileName + "...");

        Color[][] colorArray = ImageUtils.extractBytes(IMG_PATH + fileName);

//        Подробнее в классе Request
        Request enlargeRequest = new Request();

        long time = System.currentTimeMillis();

//        В новом потоке выполняем процесс задания B с исходными данными: part - кусочек массива, position - запоминаем какой кусочек обработали
        ExecutorService executor = ThreadExtensions.createColorArrayDependableThreads(
                colorArray,
                THREAD_COUNT,
                new ProcessorRunnableCallback() {
                    @Override
                    public <T> Runnable createProcessorRunnable(T part, int position) {
                        return new ProcessorB((Color[][]) part, position, enlargeRequest);
                    }
                }
        );

        //        Ожидаем выполнения всех потоков и подводим итоги
        boolean isSuccess = ThreadExtensions.awaitAllThreads(executor);

        if (isSuccess) {
            System.out.println("Время обработки = " + (System.currentTimeMillis() - time) + "\n");
            ImageUtils.saveImage(
                    enlargeRequest.getColorArray(),
                    IMG_PATH + ENLARGE_IMG_PATH + fileName
            );
        }
    }
}
