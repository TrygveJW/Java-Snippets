package no.trygvejw.util;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Iterator;

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

    // -- get numbers -- //
    public static short readShort(BufferedInputStream inputStream) throws IOException {
        return ByteBuffer.wrap(inputStream.readNBytes(2)).getShort();
    }

    public static int readInt(BufferedInputStream inputStream) throws IOException {
        return ByteBuffer.wrap(inputStream.readNBytes(4)).getInt();
    }

    public static Long readLong(BufferedInputStream inputStream) throws IOException {
        return ByteBuffer.wrap(inputStream.readNBytes(8)).getLong();
    }

    public static Float readFloat(BufferedInputStream inputStream) throws IOException {
        return ByteBuffer.wrap(inputStream.readNBytes(4)).getFloat();
    }

    public static Double readDouble(BufferedInputStream inputStream) throws IOException {
        return ByteBuffer.wrap(inputStream.readNBytes(4)).getDouble();
    }

    // -- write numbers -- //
    public static void writeShort(BufferedOutputStream outputStream, short val) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(val);
        outputStream.write(buffer.array());
    }

    public static void writeInt(BufferedOutputStream outputStream, int val) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(val);
        outputStream.write(buffer.array());
    }

    public static void writeToStream(BufferedOutputStream outputStream, Iterator<byte[]> iterator) throws IOException {
        while (iterator.hasNext()) {
            outputStream.write(iterator.next());
        }
    }

    public static void writeFileToStream(BufferedOutputStream outputStream, File file) throws IOException {
        writeFileToStream(outputStream, file, 4096);
    }

    public static void writeFileToStream(BufferedOutputStream outputStream, File file, int bufferSize) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));


        byte[] buffer = new byte[bufferSize];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    public static void writeStringToStream(BufferedOutputStream outputStream, String string) throws IOException {
        outputStream.write(string.getBytes());
    }

    public static void writeJsonToStream(BufferedOutputStream outputStream, JSONObject jsonObject) throws IOException {
        writeStringToStream(outputStream, jsonObject.toString());


    }
}
