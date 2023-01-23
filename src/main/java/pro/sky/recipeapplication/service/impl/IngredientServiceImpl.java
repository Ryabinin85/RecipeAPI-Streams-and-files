package pro.sky.recipeapplication.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.recipeapplication.model.Ingredient;
import pro.sky.recipeapplication.service.filesService.FilesServiceImpl;
import pro.sky.recipeapplication.service.interf.IngredientService;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final FilesServiceImpl filesService;

    @Value("${name.of.ingredients.data.file}")
    private String dataFileName;

    private static Long id = 0L;
    private static Map<Long, Ingredient> ingredientList = new LinkedHashMap<>();

    public IngredientServiceImpl(FilesServiceImpl filesService) {
        this.filesService = filesService;
    }


    public static Map<Long, Ingredient> getIngredientList() {
        return ingredientList;
    }

    @PostConstruct
    private void init() {
        readFromFile();
    }

    public void addIngredientFromRecipe(List<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            if (!ingredientList.containsValue(ingredient)) {
                ingredientList.put(id++, ingredient);

            }
        }
        saveToFile();
    }

    @Override
    public void addNewIngredient(Ingredient ingredient) {
        if (!ingredientList.containsValue(ingredient)) {
            ingredientList.put(id++, ingredient);

        }
        saveToFile();
    }

    @Override
    public Ingredient getIngredient(Long id) {
        if (ingredientList.isEmpty() || !ingredientList.containsKey(id)) {
            return null;
        } else {
            return ingredientList.get(id);
        }
    }

    @Override
    public String getAllIngredients() {
        return ingredientList.toString();
    }

    @Override
    public void editIngredient(long id, Ingredient ingredient) {
        if (ingredient != null && !ingredientList.isEmpty() && ingredientList.containsKey(id)) {
            ingredientList.put(id, ingredient);
            saveToFile();
        }
    }

    @Override
    public boolean deleteIngredient(long id) {
        if (ingredientList.containsKey(id)) {
            ingredientList.remove(id);
            saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllIngredients() {
        ingredientList = new LinkedHashMap<>();
        cleanFile();
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(ingredientList);
            filesService.saveToFile(json, dataFileName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        String json = filesService.readFromFile(dataFileName);
        try {
            ingredientList = new ObjectMapper().readValue(json, new TypeReference<LinkedHashMap<Long, Ingredient>>() {
            });
        }
        catch (MismatchedInputException e) {
            e.getMessage();
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void cleanFile() {
        filesService.cleanDataFile(dataFileName);
    }
}
