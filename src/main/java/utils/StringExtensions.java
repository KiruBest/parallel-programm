package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StringExtensions {
    public static String getTextFromFile(String filePath) {
        File file = new File(filePath);
        StringBuilder resultText = new StringBuilder("");

        try (FileReader reader = new FileReader(file)) {
            int symbol;
            while ((symbol = reader.read()) != -1) {
                resultText.append((char) symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultText.toString();
    }
}
