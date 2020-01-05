package model.utils;

import java.io.File;

public class FileUtils {
    public static String getFileFromPath(String filePath) {
        StringBuilder filename = new StringBuilder();
        int pos = filePath.length() - 1;

        do {
            filename.append(filePath.charAt(pos--));
        } while (filePath.charAt(pos) != '\\');

        return filename.reverse().toString();
    }

    public static void deleteDownloads() {
        File index = new File("./Downloads");
        String[] entries = index.list();
        if (entries != null) {
            for(String entry: entries){
                File currentFile = new File(index.getPath(), entry);
                currentFile.delete();
            }
        }

        index.delete();
    }
}
