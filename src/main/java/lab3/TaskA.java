package lab3;

import java.io.File;
import java.util.Objects;

import static constants.Path.IMG_PATH;

public class TaskA {
    public static void main(String[] args) {
        try {
            File imgFolder = new File(IMG_PATH);
            File[] listOfFiles = Objects.requireNonNull(imgFolder.listFiles());

            for (int i = 0; i < 1; i++) {
                System.out.println("#" + i + "\n");

                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        new ImageSample(fileName);
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}