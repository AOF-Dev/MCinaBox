package com.aof.mcinabox.network.model;

import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Downloadable {
    private static final String TAG = "Downloadable";

    private final URL url;
    private final File target;
    private final boolean forceDownload;
    private long startTime;
    protected int numAttempts;
    private long expectedSize;
    private long endTime;

    public Downloadable(URL remoteFile, File localFile, boolean forceDownload) {
        this.url = remoteFile;
        this.target = localFile;
        this.forceDownload = forceDownload;
    }

    public long getExpectedSize() {
        return this.expectedSize;
    }

    public void setExpectedSize(long expectedSize) {
        this.expectedSize = expectedSize;
    }

    public static String getDigest(File file, String algorithm, int hashLength) {
        DigestInputStream stream = null;
        try {
            int read;
            stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance(algorithm));
            byte[] buffer = new byte[65536];
            do {
                read = stream.read(buffer);
            } while (read > 0);
        } catch (Exception ignored) {
            return null;
        } finally {
            closeSilently(stream);
        }
        return String.format("%1$0" + hashLength + "x", new BigInteger(1, stream.getMessageDigest().digest()));
    }

    public abstract String download() throws IOException;

    protected void updateExpectedSize(HttpURLConnection connection) {
        if (this.expectedSize == 0L) {
            setExpectedSize(connection.getContentLength());
        }
    }

    public URL getUrl() {
        return this.url;
    }

    public File getTarget() {
        return this.target;
    }

    public boolean shouldIgnoreLocal() {
        return this.forceDownload;
    }

    public int getNumAttempts() {
        return this.numAttempts;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException iOException) {
            }
    }

    public static String copyAndDigest(InputStream inputStream, OutputStream outputStream, String algorithm, int hashLength) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            closeSilently(inputStream);
            closeSilently(outputStream);
            throw new RuntimeException("Missing Digest." + algorithm, e);
        }
        byte[] buffer = new byte[65536];
        try {
            int read = inputStream.read(buffer);
            while (read >= 1) {
                digest.update(buffer, 0, read);
                outputStream.write(buffer, 0, read);
                read = inputStream.read(buffer);
            }
        } finally {
            closeSilently(inputStream);
            closeSilently(outputStream);
        }
        return String.format("%1$0" + hashLength + "x", new BigInteger(1, digest.digest()));
    }

    protected void ensureFileWritable(File target) {
        if (target.getParentFile() != null && !target.getParentFile().isDirectory()) {
            Log.i(TAG, "ensureFileWritable: Making directory " + target.getParentFile());
            if (!target.getParentFile().mkdirs() &&
                    !target.getParentFile().isDirectory())
                throw new RuntimeException("Could not create directory " + target.getParentFile());
        }
        if (target.isFile() && !target.canWrite())
            throw new RuntimeException("Do not have write permissions for " + target + " - aborting!");
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return "Downloading " + getTarget().getName();
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
