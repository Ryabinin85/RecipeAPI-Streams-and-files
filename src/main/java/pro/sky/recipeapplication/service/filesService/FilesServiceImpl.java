package pro.sky.recipeapplication.service.filesService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.recipeapplication.service.interf.FilesService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FilesServiceImpl implements FilesService {

    @Value("${path.to.data.file}")
    private String dataFilePath;

    @Override
    public void saveToFile(String json, String dataFileName) {
        try {
            cleanDataFile(dataFileName);
            Files.writeString(Path.of(dataFilePath, dataFileName), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String readFromFile(String dataFileName) {
        try {
            return Files.readString(Path.of(dataFilePath, dataFileName));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanDataFile(String dataFileName) {
        try {
            Path path = Path.of(dataFilePath, dataFileName);
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getDataFile(String dataFileName) {
        return new File(dataFilePath + "/" + dataFileName);
    }

    @Override
    public Path createTempFile(String suffix) {
        try {
            return Files.createTempFile(Path.of(dataFilePath), "temp", suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
