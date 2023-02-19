package lab2;

import utils.ImageUtils;
import utils.NumberedColorArray;
import utils.Request;

import java.awt.*;

class ProcessorB implements Runnable {
    private final Color[][] colorArray;
    private final int position;
    private final Request enlargeRequest;

    ProcessorB(Color[][] colorArray, int position, Request enlargeRequest) {
        this.colorArray = colorArray;
        this.position = position;
        this.enlargeRequest = enlargeRequest;
    }

    @Override
    public void run() {
        Color[][] invertedImage = ImageUtils.enlargeImage(colorArray);
        enlargeRequest.addColorArray(new NumberedColorArray(invertedImage, position));
    }
}
