package com.mwstys.graphtask.repositories.file;

import com.rafalzajfert.androidlogger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * By Mikołaj Styś on 16/08/2015 16:32
 */
public class ZipUtils {

    private static final int BUFFER_SIZE = 1024;

    public static boolean compress(String destinationPath, String... sourceFilePaths) {
        boolean result = true;
        try {
            Path destination = new Path(destinationPath);
            ZipOutputStream out = new ZipOutputStream(
                    new BufferedOutputStream(destination.getOutputStream()));

            byte data[] = new byte[BUFFER_SIZE];
            for (String sourceFilePath : sourceFilePaths) {
                Path sourcePath = new Path(sourceFilePath);
                Logger.verbose("Compress", "Adding: " + sourcePath);
                BufferedInputStream origin =
                        new BufferedInputStream(sourcePath.getInputStream(), BUFFER_SIZE);
                ZipEntry entry = new ZipEntry(sourceFilePath
                        .substring(sourceFilePath.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int len;
                while ((len = origin.read(data, 0, BUFFER_SIZE)) > 0) {
                    out.write(data, 0, len);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            Logger.warning(e.getMessage());
            result = false;
        }
        return result;
    }

    public static boolean decompress(String sourcePath, String destinationDirectory) {
        byte[] buffer = new byte[BUFFER_SIZE];
        boolean result = true;
        try {
            Path source = new Path(sourcePath);
            Path destination = new Path(destinationDirectory);
            destination.createDirectory();
            ZipInputStream zis = new ZipInputStream(source.getInputStream());
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                Path newFile = new Path(destination, fileName);
                FileOutputStream fos = newFile.getOutputStream();
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (Exception e) {
            Logger.warning(e.getMessage());
            result = false;
        }
        return result;
    }
}