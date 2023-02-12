package lab2;

import utils.ImageUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static constants.Path.*;

public class A {
    private static final int THREAD_COUNT = 8;

    static class ImageProcessor implements Runnable {
        private final Color[][] colorArray;
        private final int position;
        private final Request invertedRequest;
        private final Request contrastedRequest;

        ImageProcessor(Color[][] colorArray, int position, Request invertedRequest, Request contrastedRequest) {
            this.colorArray = colorArray;
            this.position = position;
            this.invertedRequest = invertedRequest;
            this.contrastedRequest = contrastedRequest;
        }

        @Override
        public void run() {
            try {
                Color[][] invertedImage = ImageUtils.invertedImage(colorArray);
                Color[][] contrastedImage = ImageUtils.transformImage(colorArray, ImageUtils.contrastMatrix);
                invertedRequest.addColorArray(new NumberedColorArray(invertedImage, position));
                contrastedRequest.addColorArray(new NumberedColorArray(contrastedImage, position));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
        Request contrastedRequest = new Request();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long time = System.currentTimeMillis();

        int partWidth = colorArray.length / THREAD_COUNT;
        for (int j = 0; j < THREAD_COUNT; j++) {
            int startPosition = partWidth * j;
            int endPosition = partWidth * j + partWidth;
            Color[][] part = Arrays.copyOfRange(colorArray, startPosition, endPosition);

            Runnable processor = new ImageProcessor(part, j, invertedRequest, contrastedRequest);
            executor.execute(processor);
        }

        executor.shutdown();
        boolean isSuccess = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        if (isSuccess) {
            ImageUtils.saveImage(
                    invertedRequest.getColorArray(),
                    IMG_PATH + INVERTED_IMG_PATH + fileName
            );
            ImageUtils.saveImage(
                    contrastedRequest.getColorArray(),
                    IMG_PATH + CONTRAST_IMG_PATH + fileName
            );
            System.out.println("Время обработки = " + (System.currentTimeMillis() - time) + "\n");
        }
    }
}
