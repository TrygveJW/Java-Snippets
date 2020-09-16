package no.trygvejw.util;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamUtils {

    public static JSONObject readJsonObject(BufferedInputStream inputStream, int byteSize) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        JSONObject jsonObject = new JSONObject(
                new JSONTokener(
                        new String(inputStream.readNBytes(byteSize))));


        return jsonObject;
    }

    public static void readImageToFile(BufferedInputStream inputStream, int byteSize, File savePath) throws IOException {

        FileOutputStream outputStream = new FileOutputStream(savePath);

        int remaining = byteSize;
        int bufferSize = 4096;

        byte[] buffer = new byte[bufferSize];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer, 0, Math.min(remaining, bufferSize))) != -1) {
            remaining -= bufferSize;

            outputStream.write(buffer, 0, bytesRead);
        }
    }
}
