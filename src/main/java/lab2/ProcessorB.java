package lab2;

import utils.ImageUtils;
import utils.NumberedColorArray;
import utils.Request;

import java.awt.*;

class ProcessorB implements Runnable {
    private final Color[][] colorArray;
    private final int position;
    private final Request invertedRequest;

    ProcessorB(Color[][] colorArray, int position, Request invertedRequest) {
        this.colorArray = colorArray;
        this.position = position;
        this.invertedRequest = invertedRequest;
    }

    @Override
    public void run() {
        Color[][] invertedImage = ImageUtils.enlargeImage(colorArray);
        invertedRequest.addColorArray(new NumberedColorArray(invertedImage, position));
    }
}
