package mich;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class contains all the utilities necessary to read and write files:
 * read a file as a Byte[]
 * convert Byte[] to byte[]
 * write to a file given a file and a byte[]
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/24/19
 **/
public class FileUtils {

    public static Byte[] readFileAsBytes(File file) {
        Byte[] data;
        byte[] b = null;
        try {
            b = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = new Byte[b.length];
        int i = 0;
        for (byte bb: b) {
            data[i++] = bb;
        }
        return data;
    }

    public static void writeFile(String path, byte[] bytes) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] convert(Byte[] d1) {
        byte[] b = new byte[d1.length];
        int i = 0;
        for (Byte bb: d1) {
            b[i++] = bb;
        }
        return b;
    }

    public static Byte[] convert(byte[] d1) {
        Byte[] b = new Byte[d1.length];
        int i = 0;
        for (byte bb: d1) {
            b[i++] = bb;
        }
        return b;
    }
}
