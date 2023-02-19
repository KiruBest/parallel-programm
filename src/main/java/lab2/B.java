package lab2;

import utils.ImageUtils;
import utils.Request;
import utils.ThreadExtensions;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import static constants.Path.*;

public class B {
    private static final int THREAD_COUNT = 1;

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

        Request invertedRequest = new Request();

        long time = System.nanoTime();

        ExecutorService executor = ThreadExtensions.createThreads(
                colorArray,
                THREAD_COUNT,
                (part, position) -> new ProcessorB(part, position, invertedRequest)
        );

        boolean isSuccess = ThreadExtensions.awaitAllThreads(executor);

        if (isSuccess) {
            System.out.println("Время обработки = " + (System.nanoTime() - time) + "\n");
            ImageUtils.saveImage(
                    invertedRequest.getColorArray(),
                    IMG_PATH + ENLARGE_IMG_PATH + fileName
            );
        }
    }
}
