package com.kasperserzysko.tools;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class FileService {

    private final static String FOLDER_PATH = "D:/kasperserzysko_repository/REDstore/files/";

    public static void saveImage(MultipartFile multipartFile, Long gameId ) throws IOException {
        Path uploadPath = Paths.get(FOLDER_PATH + "/" + gameId);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(FOLDER_PATH + gameId + "/" + gameId + ".jpg");
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file!");
        }
    }

    public static byte[] getImage(Long gameId) throws IOException {
        try {
            return Files.readAllBytes(Path.of(FOLDER_PATH + gameId + "/" + gameId + ".jpg"));
        } catch (IOException e) {
            throw new IOException("Couldn't get an image!");
        }
    }
    public static void deleteFolder(Long gameId) throws IOException {
        FileUtils.deleteDirectory(new File(FOLDER_PATH + gameId));
    }
}
