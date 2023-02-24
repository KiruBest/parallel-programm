package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

//Утилиты для считывания, сохранения, преобразования картинок
public class ImageUtils {
    public static final int I_MAX = 255;
    public static final String JPG_FORMAT = "jpg";
    public static final int[][] contrastMatrix = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};

    private static final int THRESHOLD = 100;
    private static final int ENLARGE_STEP = 10;

    public static Color[][] extractBytes(String imageName) throws IOException {
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

    public static Color[][] invertedImage(Color[][] colorArray) throws IOException {
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

        return invertedColorArray;
    }

    public static Color[][] transformImage(Color[][] colorArray, int[][] transformMatrix)
            throws IOException {
        int transformMatrixSize = transformMatrix.length;
        int width = colorArray.length;
        int height = colorArray[0].length;
        Color[][] transformedColorArray = new Color[width - transformMatrixSize + 1][height - transformMatrixSize + 1];

        for (int i = 0; i <= width - transformMatrixSize; i++) {
            for (int j = 0; j <= height - transformMatrixSize; j++) {
                int bufferR = 0;
                int bufferB = 0;
                int bufferG = 0;

                for (int x = i; x < i + transformMatrixSize; x++) {
                    for (int y = j; y < j + transformMatrixSize; y++) {
                        Color sourceColor = colorArray[x][y];
                        int multiplyValue = transformMatrix[x - i][y - j];

                        bufferR += sourceColor.getRed() * multiplyValue;
                        bufferG += sourceColor.getGreen() * multiplyValue;
                        bufferB += sourceColor.getBlue() * multiplyValue;
                    }
                }

                bufferR = Math.min(Math.abs(bufferR), I_MAX);
                bufferG = Math.min(Math.abs(bufferG), I_MAX);
                bufferB = Math.min(Math.abs(bufferB), I_MAX);

                transformedColorArray[i][j] = new Color(bufferR, bufferG, bufferB);
            }
        }

        return transformedColorArray;
    }

    public static Color[][] enlargeImage(Color[][] colorArray) {
        int width = colorArray.length;
        int height = colorArray[0].length;
        Color[][] resultColorArray = new Color[width][height];

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Color color = colorArray[i][j];
                int intensity = getIntensity(color);

                if (intensity >= THRESHOLD) {
                    int startX = i - ENLARGE_STEP;
                    int startY = j - ENLARGE_STEP;
                    int endX = i + ENLARGE_STEP;
                    int endY = j + ENLARGE_STEP;

                    for (int x = startX; x < endX; ++x) {
                        for (int y = startY; y < endY; ++y) {
                            if (x >= 0 && y >= 0 && x < width && y < height) {
                                resultColorArray[x][y] = Color.WHITE;
                            }
                        }
                    }
                } else if (resultColorArray[i][j] == null) {
                    resultColorArray[i][j] = Color.BLACK;
                }
            }
        }

        return resultColorArray;
    }

    public static void saveImage(Color[][] colorArray, String fileName) throws IOException {
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

    private static int getIntensity(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        return (r + g + b) / 3;
    }
}
