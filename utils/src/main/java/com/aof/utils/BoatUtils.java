package com.aof.utils;

import android.content.res.AssetManager;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class BoatUtils {

    public static File createFile(String filePath) {
        File file = new File(filePath);
        return BoatUtils.createFile(file);
    }

    public static File createFile(File file) {
        if (file.exists()) {
            file.delete();
        }
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static byte[] readFile(String filePath) {
        return BoatUtils.readFile(new File(filePath));
    }

    public static byte[] readFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] result = new byte[(int) file.length()];
            fis.read(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeFile(File file, byte[] bytes) {
        file = BoatUtils.createFile(file);
        if (file == null) {
            return false;
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeFile(File file, String str) {
        boolean retval = false;
        retval = BoatUtils.writeFile(file, str.getBytes(StandardCharsets.UTF_8));
        return retval;
    }

    public static boolean writeFile(String outFile, String str) {
        return writeFile(new File(outFile), str);
    }

    public static boolean extractAsset(AssetManager am, String src, File targetFile) {
        targetFile = BoatUtils.createFile(targetFile);

        try (FileOutputStream fos = new FileOutputStream(targetFile);
             InputStream is = am.open(src)) {
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = is.read(buf)) != -1) {
                fos.write(buf, 0, count);
            }

            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean extractAsset(AssetManager am, String src, String target) {
        return extractAsset(am, src, new File(target));
    }

    public static void extractTarXZ(File tarFile, File destDir) {
        try (FileInputStream fis = new FileInputStream(tarFile);
             XZCompressorInputStream xzcis = new XZCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(xzcis, 1024)) {
            TarArchiveEntry entry;
            while ((entry = tais.getNextTarEntry()) != null) {
                File target = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    target.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(target)) {
                        IOUtils.copy(tais, fos);
                        fos.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void extractTarXZ(String tar, File destDir) {
        extractTarXZ(new File(tar), destDir);
    }

    public static void extractTarXZ(File tarFile, String dir) {
        extractTarXZ(tarFile, new File(dir));
    }

    public static void extractTarXZ(String tar, String dir) {
        extractTarXZ(new File(tar), new File(dir));
    }

    public static boolean setExecutable(File file) {
        boolean retval = true;
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                retval = retval && setExecutable(subFile);
            }
        }
        retval = retval && file.setExecutable(true);
        return retval;
    }

    public static boolean setExecutable(String file) {
        return setExecutable(new File(file));
    }
}
