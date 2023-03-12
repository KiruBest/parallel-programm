package lab3;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import jcuda.runtime.JCuda;
import utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static constants.Path.IMG_PATH;
import static utils.ImageUtils.createBufferedImage;

public class CUDAImage {
    private final BufferedImage inputImage;

    /**
     * The width of the image
     */
    private final int imageSizeX;

    /**
     * The height of the image
     */
    private final int imageSizeY;

    private final int[][] intensityMatrix;

    public CUDAImage(String fileName) {
        inputImage = createBufferedImage(IMG_PATH + fileName);
        assert inputImage != null;
        imageSizeX = inputImage.getWidth();
        imageSizeY = inputImage.getHeight();

        intensityMatrix = ImageUtils.getIntensityMatrixFromBufferedImage(inputImage);
    }


    //Sobel operator edge detection kernel function
//    void sobel(Pointer dataIn, Pointer dataOut) {
//        int xIndex = threadIdx.x + blockIdx.x * blockDim.x;
//        int yIndex = threadIdx.y + blockIdx.y * blockDim.y;
//        int index = yIndex * imageSizeX + xIndex;
//        int Gx = 0;
//        int Gy = 0;
//        boolean canDoIteration = xIndex > 0 && xIndex < imageSizeX - 1 && yIndex > 0 && yIndex < imageSizeY - 1;
//
//        if (canDoIteration) {
//            Gx = dataIn[(yIndex - 1) * imageSizeX + xIndex + 1]
//                    + 2 * dataIn[yIndex * imageSizeX + xIndex + 1]
//                    + dataIn[(yIndex + 1) * imageSizeX + xIndex + 1]
//                    - (dataIn[(yIndex - 1) * imageSizeX + xIndex - 1] +
//                    2 * dataIn[yIndex * imageSizeX + xIndex - 1]
//                    + dataIn[(yIndex + 1) * imageSizeX + xIndex - 1]);
//            Gy = dataIn[(yIndex - 1) * imageSizeX + xIndex - 1]
//                    + 2 * dataIn[(yIndex - 1) * imageSizeX + xIndex]
//                    + dataIn[(yIndex - 1) * imageSizeX + xIndex + 1]
//                    - (dataIn[(yIndex + 1) * imageSizeX + xIndex - 1]
//                    + 2 * dataIn[(yIndex + 1) * imageSizeX + xIndex]
//                    + dataIn[(yIndex + 1) * imageSizeX + xIndex + 1]);
//            dataOut[index] = (Math.abs(Gx) + Math.abs(Gy)) / 2;
//        }
//
}
