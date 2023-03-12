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
    private static final int ENLARGE_STEP = 2;

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
        int transformWidth = width - transformMatrixSize + 1;
        int transformHeight = height - transformMatrixSize + 1;
        Color[][] transformedColorArray = new Color[transformWidth][transformHeight];

        for (int i = 0; i < transformWidth; i++) {
            for (int j = 0; j < transformHeight; j++) {
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

    public static void saveImage(BufferedImage bufferedImage, String fileName) throws IOException {
        ImageIO.write(bufferedImage, JPG_FORMAT, new File(fileName));
    }

    private static int getIntensity(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        return (r + g + b) / 3;
    }

    /**
     * Creates a BufferedImage of with type TYPE_INT_RGB from the
     * file with the given name.
     *
     * @param fileName The file name
     * @return The image, or null if the file may not be read
     */
    public static BufferedImage createBufferedImage(String fileName) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int sizeX = image.getWidth();
        int sizeY = image.getHeight();

        BufferedImage result = new BufferedImage(
                sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
        Graphics g = result.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }

    public static int[][] getIntensityMatrixFromBufferedImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][] intensityMatrix = new int[width][height];

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Color color = new Color(image.getRGB(i, j));
                intensityMatrix[i][j] = getIntensity(color);
            }
        }

        return intensityMatrix;
    }
}
