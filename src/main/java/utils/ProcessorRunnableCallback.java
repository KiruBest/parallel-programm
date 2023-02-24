package utils;

import java.awt.*;

//Небольшая абстракция над выполняемыми процессорами
public interface ProcessorRunnableCallback {
     <T> Runnable createProcessorRunnable(T part, int position);
}
