import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Lab1 {
    private static final String IMG_PATH = "D:\\Programmist\\Java\\Works\\parallel-programm\\src\\main\\java\\img\\";
    private static final String INVERTED_IMG = "inverted\\";
    private static final String CONTRAST_IMG = "contrasted\\";

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
                        ImageUtils.invertedImage(colorArray, IMG_PATH + INVERTED_IMG + fileName);
                        ImageUtils.transformImage(colorArray, ImageUtils.contrastMatrix,
                                IMG_PATH + CONTRAST_IMG + fileName);

                        System.out.println("Время обработки = " + (System.currentTimeMillis() - time) + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
