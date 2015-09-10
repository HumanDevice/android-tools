package file;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mwstys.graphtask.repositories.file.exceptions.DirectoryExistPathException;
import com.mwstys.graphtask.repositories.file.exceptions.FileExistPathException;
import com.mwstys.graphtask.repositories.file.exceptions.FileNotExistPathException;
import com.mwstys.graphtask.repositories.file.exceptions.NotDirectoryPathException;
import com.mwstys.graphtask.repositories.file.exceptions.ReadPathException;
import com.mwstys.graphtask.repositories.file.exceptions.WritePathException;
import com.mwstys.graphtask.utils.annotation.AppContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * File wrapper making names consistent and adding some operations.
 *
 * @author Mikołaj Styś
 */
public class Path {
    //General convention: /something is a part of path.

    protected static final int BUFFER_SIZE = 1024;
    protected File file;

    public Path(File file) {
        this.file = file;
    }

    public Path(File dir, String name) {
        this(new File(dir, name));
    }

    public Path(String path) {
        this(new File(path));
    }

    public Path(String dirPath, String name) {
        this(new File(dirPath, name));
    }

    public Path(URI uri) {
        this(new File(uri));
    }

    public Path(Path destination, String fileName) {
        this(destination.toFile(), fileName);
    }

    /**
     * Converts file to Path. Inverted operation is not necessary
     */
    public static Path toPath(File file) {
        return new Path(file);
    }

    /**
     * Create path from elements
     */
    public static Path toPath(String... pathElements) {
        StringBuilder builder = new StringBuilder();
        for (String element : pathElements) {
            if (!element.startsWith("/")) {
                builder.append("/");
            }
            builder.append(element);
            if (element.endsWith("/")) {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
        return new Path(builder.toString());
    }

    /**
     * Check if string path exist
     */
    public static boolean isExists(@NonNull String path) {
        return new Path(path).isExists();
    }

    /**
     * Get SD card directory
     */
    public static String getSdDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Get SD app files directory
     *
     * @throws ReadPathException if cannot be read
     */
    public static String getAppFilesSdDir(@AppContext Context context) {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) {
            throw new ReadPathException("Cannot read ExternalFilesDir!");
        }
        return dir.getAbsolutePath();
    }

    /**
     * Get app cache directory
     *
     * @throws ReadPathException if cannot be read
     */
    public static String getCacheDir(@AppContext Context context) {
        File dir = context.getExternalCacheDir();
        if (dir == null) {
            throw new ReadPathException("Cannot read CacheDir!");
        }
        return dir.getAbsolutePath();
    }

    /**
     * Creates directory
     *
     * @throws FileExistPathException - if already exist
     */
    public static void createDirectory(@NonNull String path) {
        new Path(path).createDirectory();
    }

    /**
     * Creates file
     *
     * @throws FileExistPathException if already exist
     * @throws WritePathException     if cannot write
     */
    public static void createFile(@NonNull String path) {
        new Path(path).createFile();
    }

    /**
     * Delete all files recursively
     */
    public static void delete(String path) {
        new Path(path).delete();
    }

    public static void copy(String sourcePath, String destinationPath) {
        new Path(sourcePath).copy(destinationPath);
    }

    /**
     * Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to read
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    @NonNull
    /**
     * @see #getFilesInPath(String) getFilesInPath(String)
     */
    public static String[] getPathsList(String path, @Nullable FilenameFilter filter) {
        return new Path(path).getPathsList(filter);
    }

    @NonNull
    /**
     * @see #getFilesInPath(String) getFilesInPath(String)
     */
    public static Path[] getFilesList(String path, @Nullable FilenameFilter filter) {
        return new Path(path).getFilesList(filter);
    }

    /**
     * In case of changing implementation
     */
    public File toFile() {
        return file;
    }

    /**
     * Makes exist command more uniform
     */
    public boolean isExists() {
        return file.exists();
    }

    /**
     * @return !isExist()
     */
    public boolean isNotExists() {
        return file.exists();
    }

    /**
     * Creates directory
     *
     * @throws FileExistPathException - if already exist
     */
    public void createDirectory() {
        boolean failed = false;
        if (!file.exists()) {
            failed = !file.mkdirs();
        } else if (file.isFile()) {
            throw new FileExistPathException(getStringPath());
        }
        if (failed) {
            throw new WritePathException("Cannot create folders!");
        }
    }

    /**
     * Creates file
     *
     * @throws FileExistPathException if already exist
     * @throws WritePathException     if cannot write
     */
    public void createFile() {
        try {
            if (!file.exists()) {
                if (getParentPath().isNotExists()) {
                    boolean failed = !file.getParentFile().mkdirs();
                    if (failed) {
                        throw new WritePathException("Cannot create directory: " + file.getParent());
                    }
                }
                boolean failed = !file.createNewFile();
                if (failed) {
                    throw new WritePathException("Cannot create: " + getStringPath());
                }
            } else if (file.isDirectory()) {
                throw new DirectoryExistPathException(getStringPath());
            }
        } catch (IOException e) {
            throw new WritePathException("Cannot write to file: " + getStringPath(), e);
        }
    }

    /**
     * Delete all files recursively. If not exist - do nothing.
     */
    public void delete() {
        if (file.exists()) {
            if (file.isDirectory()) {
                String dir = getStringPath();
                String[] children = file.list();
                for (String child : children) {
                    delete(dir + child);
                }
            }
            boolean failed = !file.delete();
            if (failed) {
                throw new WritePathException("Cannot delete: " + getStringPath());
            }
        }
    }

    /**
     * Copy recursively into specific directory
     *
     * @param destinationPath path where the path will be copied to
     */
    public void copy(String destinationPath) {
        if (file.isDirectory()) {
            String dir = getStringPath();
            String[] children = file.list();
            for (String child : children) {
                copy(dir + child, destinationPath + child);
            }
        } else {
            Path destination = new Path(destinationPath);
            destination.getParentPath().createDirectory();
            InputStream in = this.getInputStream();
            OutputStream out = destination.getOutputStream();
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                throw new WritePathException("Cannot copy " + getStringPath() + " into " + destinationPath);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    /**
     *  List all file paths in directory
     *  @throw NotDirectoryPathException when is not a directory
     */
    public String[] getPathsList(@Nullable FilenameFilter filter) {
        String[] result;
        if (!file.isDirectory()) {
            throw new NotDirectoryPathException(getStringPath());
        }
        if (filter != null) {
            result = file.list(filter);
        } else {
            result = file.list();
        }
        for (int i = 0; i < result.length; i++) {
            result[i] = getStringPath() + result[i];
        }
        return result;
    }

    @NonNull
    /**
     *  List all file paths in directory
     *  @throw NotDirectoryPathException when is not a directory
     */
    public Path[] getFilesList(@Nullable FilenameFilter filter) {
        String[] tmp = getPathsList(filter);
        Path[] result = new Path[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            result[i] = new Path(getStringPath() + tmp[i]);
        }
        return result;
    }

    /**
     * @return file size in bytes
     */
    public long getSize() {
        return file.length();
    }

    /**
     * Modification date is not affected after renaming and copying
     *
     * @return unix millis
     */
    public long getModificationDate() {
        return file.lastModified();
    }

    public Path getParentPath() {
        return Path.toPath(file.getParentFile());
    }

    /*
     * @param append if data should be appended
     * @return
     */
    public FileOutputStream getOutputStream(boolean append) {
        try {
            return new FileOutputStream(file, append);
        } catch (FileNotFoundException e) {
            throw new FileExistPathException(getStringPath());
        }
    }

    /**
     * @see #getOutputStream(boolean) getOutputStream(false)
     */
    public FileOutputStream getOutputStream() {
        return getOutputStream(false);
    }

    /**
     * @throws FileNotExistPathException if file not exist
     */
    public FileInputStream getInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new FileNotExistPathException(getStringPath());
        }
    }

    @Override
    public String toString() {
        return getStringPath();
    }

    /**
     * @return String representation of String
     */
    public String getStringPath() {
        return file.getAbsolutePath();
    }

    /**
     * get file reader for Text operations
     *
     * @throws FileNotExistPathException
     */
    public FileReader getFileReader() {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new FileNotExistPathException(getStringPath());
        }
    }

    /**
     * get file writer for Text operations
     *
     * @throws FileNotExistPathException
     */
    public FileWriter getFileWriter() {
        return getFileWriter(false);
    }

    /**
     * get file writer for Text operations
     *
     * @throws FileNotExistPathException
     */
    public FileWriter getFileWriter(boolean append) {
        try {
            return new FileWriter(file, append);
        } catch (IOException e) {
            throw new FileNotExistPathException(getStringPath());
        }
    }

    public boolean isFile() {
        return file.isFile();
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public String getName() {
        return file.getName();
    }

    public void setName(String newName) {
        boolean success = file.renameTo(new File(file.getParentFile().getAbsoluteFile(), newName));
        if (!success) {
            throw new WritePathException("Cannot rename " + getParentPath().getStringPath()
                    + " : " + file.getName() + " to " + newName);
        }
    }

    /**
     * @see #movePath(String, String) movePath(String, null)
     */
    public void movePath(String destinationPath) {
        movePath(destinationPath, null);
    }

    public void movePath(String destinationPath, @Nullable String newName) {
        boolean success = file.renameTo(new File(destinationPath, file.getName()));
        if (!success) {
            throw new WritePathException("Cannot move " + getParentPath().getStringPath()
                    + " : " + file.getName() + " to " + newName);
        }
        if (newName != null) {
            setName(newName);
        }
    }
}