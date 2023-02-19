package utils;

import java.awt.*;

public interface ProcessorRunnableCallback {
    Runnable createProcessorRunnable(Color[][] part, int position);
}
