package com.example.oga;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FtpServerConnection {
    public static FTPClient mFTPClient = null;
    List<String> files = new ArrayList<>();
    public boolean ftpConnect(String host, String username, String password, int port) {
        try {
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(InetAddress.getByName(host),port);
            mFTPClient.setConnectTimeout(1000000000);

            boolean status = mFTPClient.login(username, password);
            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
                FTPFile[] mFileArray = mFTPClient.listFiles();
                for (FTPFile file:mFileArray){
                    System.out.println("File From FTP Server is ::"+file.getName());
                    files.add(file.getName());
                }
                return status;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error: could not connect to host " + host);
        }
        return false;
    }

    public List<String> getFilesNames(){
        return files;
    }
    public boolean ftpDisconnect() {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }
}
