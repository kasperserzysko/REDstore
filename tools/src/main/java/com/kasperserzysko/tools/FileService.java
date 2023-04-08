package com.kasperserzysko.tools;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileService {

    private final static String FOLDER_PATH = "D:/kasperserzysko_repository/REDstore/files/";

    public static void saveImage(MultipartFile multipartFile, Long gameId) throws IOException {
        String path = (gameId) + "/" + multipartFile.getOriginalFilename();
        saveFile(multipartFile, path, gameId);
    }
    public static void saveTitleImage(MultipartFile multipartFile, Long gameId ) throws IOException {
        String path = (gameId) + "/" + gameId + ".jpg";
        saveFile(multipartFile, path, gameId);
    }

    public static byte[] getImage(Long gameId) throws IOException {
        try {
            return Files.readAllBytes(Path.of(FOLDER_PATH + gameId));
        } catch (IOException e) {
            throw new IOException("Couldn't get an image!");
        }
    }



    private static void saveFile(MultipartFile multipartFile, String filePathName, Long gameId) throws IOException {
        Path uploadPath = Paths.get(FOLDER_PATH + "/" + gameId);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(FOLDER_PATH + filePathName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file!");
        }
    }
}
