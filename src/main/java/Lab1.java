import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Lab1 {
    private static final int I_MAX = 255;
    private static final String IMG_PATH = "D:\\Programmist\\Java\\Works\\parallel-programm\\src\\main\\java\\img\\";
    private static final String BASE_IMG = "example.jpg";
    private static final String INVERTED_IMG = "inverted.jpg";
    private static final String CONTRAST_IMG = "contrast_image.jpg";
    private static final String JPG_FORMAT = "jpg";
    private static final int MATRIX_SIZE = 3;
    private static final int[][] CONTRAST_MATRIX = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};

    public static void main(String[] args) {
        Lab1 lab = new Lab1();
        try {
            Color[][] colorArray = lab.extractBytes(IMG_PATH + BASE_IMG);
            Color[][] invertedColorArray = lab.invertedImage(colorArray);
            lab.increaseContrast(colorArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Color[][] extractBytes(String imageName) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(imageName));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        Color[][] pixelMatrix = new Color[width][height];

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Color color = new Color(bufferedImage.getRGB(i, j));
                pixelMatrix[i][j] = color;
            }
        }

        return pixelMatrix;
    }

    public Color[][] invertedImage(Color[][] colorArray) throws IOException {
        int width = colorArray.length;
        int height = colorArray[0].length;
        Color[][] invertedColorArray = new Color[width][height];

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Color sourceColor = colorArray[i][j];
                int invertedR = I_MAX - sourceColor.getRed();
                int invertedG = I_MAX - sourceColor.getGreen();
                int invertedB = I_MAX - sourceColor.getBlue();
                Color invertedColor = new Color(invertedR, invertedG, invertedB);
                invertedColorArray[i][j] = invertedColor;
            }
        }

        saveImage(invertedColorArray, BASE_IMG + INVERTED_IMG);

        return invertedColorArray;
    }

    public void increaseContrast(Color[][] colorArray) throws IOException {
        int width = colorArray.length;
        int height = colorArray[0].length;
        int rowIteration = -1;
        int colIteration = -1;
        Color[][] exitColorArray = new Color[width - MATRIX_SIZE + 1][height - MATRIX_SIZE + 1];

        for (int arrayRow = 0; arrayRow <= width - 2; arrayRow += 3) {
            rowIteration += 1;
            for (int arrayCol = 0; arrayCol <= height - 2; arrayCol += 3) {
                colIteration += 1;
                int[] buffer = {0, 0, 0};
                for (int matrixRow = 0; matrixRow < MATRIX_SIZE; ++matrixRow) {
                    int row = arrayRow - MATRIX_SIZE * rowIteration;
                    int col = arrayCol - MATRIX_SIZE * colIteration;
                    for (int matrixCol = 0; matrixCol < MATRIX_SIZE; ++matrixCol) {
                        if (row == matrixRow & col == matrixCol) {
                            Color currentColor = colorArray[arrayRow + row][arrayCol + col];
                            int matrixValue = CONTRAST_MATRIX[matrixRow][matrixCol];

                            int multipleR = currentColor.getRed() * matrixValue;
                            int multipleG = currentColor.getGreen() * matrixValue;
                            int multipleB = currentColor.getBlue() * matrixValue;

                            buffer[0] += multipleR;
                            buffer[1] += multipleG;
                            buffer[2] += multipleB;
                        }
                    }
                }
//                exitColorArray[][] = new Color(Math.abs(buffer[0]), Math.abs(buffer[1]), Math.abs(buffer[2]));
                colIteration = -1;
            }
        }

        saveImage(exitColorArray, IMG_PATH + CONTRAST_IMG);
    }

    private void saveImage(Color[][] colorArray, String fileName) throws IOException {
        int width = colorArray.length;
        int height = colorArray[0].length;
        BufferedImage bufferedImage = new BufferedImage(width, height, TYPE_INT_RGB);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Color color = colorArray[i][j];
                bufferedImage.setRGB(i, j, color.getRGB());
            }
        }

        ImageIO.write(bufferedImage, JPG_FORMAT, new File(fileName));
    }
}
