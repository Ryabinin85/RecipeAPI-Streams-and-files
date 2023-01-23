package pro.sky.recipeapplication.service.interf;

public interface FilesService {

    void saveToFile(String json, String dataFileName);

    String readFromFile(String dataFileName);

    void cleanDataFile(String dataFileName);
}

