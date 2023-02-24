package utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Небольшие расширения для создания ExecutorService, который выполняет callback в заданиях А и Б
public class ThreadExtensions {
    public static ExecutorService createColorArrayDependableThreads(
            Color[][] colorArray,
            int threadCount,
            ProcessorRunnableCallback callback) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        int partWidth = colorArray.length / threadCount;
        for (int j = 0; j < threadCount; j++) {
            int startPosition = partWidth * j;
            int endPosition = partWidth * j + partWidth;
            Color[][] part = Arrays.copyOfRange(colorArray, startPosition, endPosition);

            Runnable processor = callback.createProcessorRunnable(part, j);
            executor.execute(processor);
        }
        return executor;
    }

    public static boolean awaitAllThreads(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        return executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }
}
