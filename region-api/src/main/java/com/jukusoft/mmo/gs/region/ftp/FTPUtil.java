package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.logger.Log;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;

public class FTPUtil {

    protected static final String LOG_TAG = "FTPUtil";

    protected FTPUtil () {
        //
    }

    /**
     * Download a single file from the FTP server
     *
     * @param ftpClient an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param remoteFilePath path of the file on the server
     * @param savePath path of directory where the file will be stored
     * @return true if the file was downloaded successfully, false otherwise
     * @throws IOException if any network or IO error occurred.
     */
    public static boolean downloadSingleFile(FTPClient ftpClient,
                                             String remoteFilePath, String savePath) throws IOException {
        File downloadFile = new File(savePath);

        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdir();
        }

        OutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(downloadFile));

        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        } catch (IOException e) {
            Log.w(LOG_TAG, "IOException while downloading single file " + remoteFilePath + " stored to file " + savePath + ": ", e);
            throw e;
        } finally {
            outputStream.close();
        }
    }

    /**
     * Download a whole directory from a FTP server.
     * @param ftpClient an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param remoteDir Path of the remote directory being downloaded.
     * @param saveDir path of directory where the whole remote directory will be
     * downloaded and saved.
     *
     * @throws IOException if any network or IO error occurred.
     */
    public static void downloadDirectory(FTPClient ftpClient, String remoteDir, String saveDir) throws IOException {
        downloadDirectory(ftpClient, remoteDir, "", saveDir);
    }

    protected static void downloadDirectory(FTPClient ftpClient, String remoteDir, String parentDir, String saveDir) throws IOException {
        if (!saveDir.endsWith("/")) {
            throw new IllegalArgumentException("saveDir has to end with slash '/'!");
        }

        String dirToList = remoteDir + (parentDir.isEmpty() ? "" : "/" + parentDir);

        //list files & directories in current dir
        org.apache.commons.net.ftp.FTPFile[] subFiles = ftpClient.listFiles(dirToList);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();

                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }

                String filePath = remoteDir + "/" + parentDir + currentFileName;

                if (aFile.isDirectory()) {
                    String dirPath = saveDir + parentDir + currentFileName;
                    File newDir = new File(dirPath);

                    boolean created = newDir.mkdirs();
                    if (created) {
                        Log.d(LOG_TAG, "CREATED the directory: " + dirPath);
                    } else {
                        Log.d(LOG_TAG, "COULD NOT create the directory: " + dirPath);
                    }

                    // download the sub directory
                    downloadDirectory(ftpClient, remoteDir, parentDir + currentFileName + "/",
                            saveDir);
                } else {
                    String savePath = saveDir + parentDir + currentFileName;

                    // download the file
                    boolean success = downloadSingleFile(ftpClient, filePath,
                            savePath);
                    if (success) {
                        Log.d(LOG_TAG, "DOWNLOADED the file: " + filePath + " and saved to: " + savePath);
                    } else {
                        Log.d(LOG_TAG, "COULD NOT download the file: "
                                + filePath);
                    }
                }
            }
        }
    }

}
