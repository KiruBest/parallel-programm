package lab1;

import utils.ImageUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static constants.Path.*;

public class Lab1 {
    public static void main(String[] args) {
        try {
            File imgFolder = new File(IMG_PATH);
            File[] listOfFiles = Objects.requireNonNull(imgFolder.listFiles());

            for (int i = 0; i < 3; i++) {
                System.out.println("#" + i + "\n");

                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        System.out.println("Обработка файла: " + fileName + "...");

                        long time = System.currentTimeMillis();
                        Color[][] colorArray = ImageUtils.extractBytes(IMG_PATH + fileName);

                        Color[][] invertedColorArray = ImageUtils.invertedImage(colorArray);
                        ImageUtils.saveImage(invertedColorArray, IMG_PATH + INVERTED_IMG_PATH + fileName);

                        Color[][] transformedColorArray = ImageUtils.transformImage(colorArray,
                                ImageUtils.contrastMatrix);
                        ImageUtils.saveImage(transformedColorArray, IMG_PATH + CONTRAST_IMG_PATH + fileName);

                        System.out.println("Время обработки = " + (System.currentTimeMillis() - time) + "\n");
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
