package game.utils;

import com.cubes.BlockTerrainControl;
import com.cubes.network.CubesSerializer;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * Util class for saving Cubes-maps to file as byte data.
 * Bytes can be read from file when Cubes-map is needed.
 */
public class SerializerUtil {

    public static void writeToFile(BlockTerrainControl blockTerrain, String fileName) {

        byte[] serializedBlockTerrain = CubesSerializer.writeToBytes(blockTerrain);
        BufferedOutputStream bos;

        try {
            bos = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
            bos.write(serializedBlockTerrain);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            System.out.println("Writing to file failed:\n" + e);
        }
    }

    public static byte[] readBytesFromFile(String path) {
        File file = new File(path);
        byte[] fileData = new byte[(int) file.length()];

        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
        } catch (Exception e) {
            System.out.println("Reading bytes from file failed:\n" + e);
        }

        return fileData;
    }
}
