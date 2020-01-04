package model.utils;

public class FileUtils {
    public static String getFileFromPath(String filePath) {
        StringBuilder filename = new StringBuilder();
        int pos = filePath.length() - 1;

        do {
            filename.append(filePath.charAt(pos--));
        } while (filePath.charAt(pos) != '\\');

        return filename.reverse().toString();
    }
}
