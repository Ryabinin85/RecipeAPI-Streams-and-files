package pro.sky.recipeapplication.service.interf;

import java.io.File;
import java.nio.file.Path;

public interface FilesService {

    void saveToFile(String json, String dataFileName);

    String readFromFile(String dataFileName);

    void cleanDataFile(String dataFileName);

    File getDataFile(String dataFileName);

    Path createTempFile(String suffix);
}

