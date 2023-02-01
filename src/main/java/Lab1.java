import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Lab1 {
    private static final int I_MAX = 255;
    private static final String IMG_PATH = "D:\\Programmist\\Java\\Works\\parallel-programm\\src\\main\\java\\img\\";
    private static final String BASE_IMG = "sample.jpg";
    private static final String INVERTED_IMG = "inverted.jpg";
    private static final String JPG_FORMAT = "jpg";

    public static void main(String[] args) {
        Lab1 lab = new Lab1();
        try {
            Color[][] colorArray = lab.extractBytes(IMG_PATH + BASE_IMG);
            Color[][] invertedColorArray = lab.invertedImage(colorArray);
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
        BufferedImage bufferedImage = new BufferedImage(width, height, TYPE_INT_RGB);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Color sourceColor = colorArray[i][j];
                int invertedR = I_MAX - sourceColor.getRed();
                int invertedG = I_MAX - sourceColor.getGreen();
                int invertedB = I_MAX - sourceColor.getBlue();
                Color invertedColor = new Color(invertedR, invertedG, invertedB);
                invertedColorArray[i][j] = invertedColor;
                bufferedImage.setRGB(i, j, invertedColor.getRGB());
            }
        }

        ImageIO.write(bufferedImage, JPG_FORMAT, new File(BASE_IMG + INVERTED_IMG));

        return invertedColorArray;
    }
}
