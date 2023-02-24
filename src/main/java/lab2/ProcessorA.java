package lab2;

import utils.ImageUtils;
import utils.NumberedColorArray;
import utils.Request;

import java.awt.*;
import java.io.IOException;

//Процессор для многопоточного вычисления задания A
class ProcessorA implements Runnable {
    private final Color[][] colorArray;
    private final int position;
    private final Request invertedRequest;
    private final Request contrastedRequest;

    ProcessorA(Color[][] colorArray, int position, Request invertedRequest, Request contrastedRequest) {
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
