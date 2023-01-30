package pro.sky.recipeapplication.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.recipeapplication.model.Ingredient;
import pro.sky.recipeapplication.model.Recipe;
import pro.sky.recipeapplication.service.filesService.FilesServiceImpl;
import pro.sky.recipeapplication.service.interf.IngredientService;
import pro.sky.recipeapplication.service.interf.RecipeService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static pro.sky.recipeapplication.service.impl.IngredientServiceImpl.getIngredientList;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final FilesServiceImpl filesService;

    @Value("${name.of.recipes.data.file}")
    private String dataFileName;

    private final IngredientService ingredients;

    private static Map<Long, Recipe> recipeList = new LinkedHashMap<>();
    private static Long id = 0L;

    public RecipeServiceImpl(IngredientService ingredients, FilesServiceImpl filesService) {
        this.ingredients = ingredients;
        this.filesService = filesService;
    }

    @PostConstruct
    private void init() {
        readFromFile();
    }

    @Override
    public String getDataFileName() {
        return dataFileName;
    }

    @Override
    public boolean addRecipe(Recipe recipe) {
        if (!recipeList.containsValue(recipe)) {
            recipeList.put(id++, recipe);
            ingredients.addIngredientFromRecipe(recipe.getIngredients());
            saveToFile();
            return true;
        } else return false;
    }

    @Override
    public boolean addRecipeList(List<Recipe> recipe) {
        boolean flag = false;
        for (Recipe newRecipe : recipe) {
            if (!recipeList.containsValue(newRecipe)) {
                recipeList.put(id++, newRecipe);
                ingredients.addIngredientFromRecipe(newRecipe.getIngredients());
                flag = true;
            }
        }
        saveToFile();
        return flag;
    }

    @Override
    public Recipe getRecipe(Long id) {
        if (recipeList.isEmpty() || !recipeList.containsKey(id)) {
            return null;
        } else {
            return recipeList.get(id);
        }
    }

    @Override
    public String getAllRecipes() {
        return recipeList.toString();
    }

    @Override
    public void editRecipe(long id, Recipe recipe) {
        if (recipe != null && !recipeList.isEmpty() && recipeList.containsKey(id)) {
            recipeList.put(id, recipe);
            saveToFile();
        }
    }

    @Override
    public boolean deleteRecipe(long id) {
        if (recipeList.containsKey(id)) {
            recipeList.remove(id);
            saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllRecipe() {
        recipeList = new LinkedHashMap<>();
        cleanFile();
    }

    @Override
    public List<Recipe> findRecipeByIngredient(long id) {
        if (getIngredientList().get(id) != null) {
            Ingredient ingredient = getIngredientList().get(id);
            return recipeList.values()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(x -> x.getIngredients().contains(ingredient))
                    .toList();
        } else return null;
    }


    @Override
    public Path createReport(String recipeName) throws IOException, NoSuchElementException {
        int counter = 1;
        if (!findRecipeByName(recipeName)) return null;

        Path report = filesService.createTempFile("report");
        Recipe recipe = recipeList.values()
                .stream()
                .filter(o -> o.getRecipeName().equals(recipeName))
                .findAny()
                .orElseThrow();

        try (Writer writer = Files.newBufferedWriter(report, StandardOpenOption.APPEND)) {
            writer.append(recipe.getRecipeName()).append("\n")
                    .append("Время приготовления: ")
                    .append(String.valueOf(recipe.getCookTime()))
                    .append(" минут\n")
                    .append("Ингредиенты: \n");

            for (Ingredient ingredient : recipe.getIngredients()) {
                writer.append(ingredient.toString()).append("\n");
            }

            writer.append("Инструкция приготовления: \n");
            for (String str : recipe.getCookingInstruction().values()) {
                writer.append(String.valueOf(counter))
                        .append(" ")
                        .append(str).append("\n");
                ++counter;
            }
        }
        return report;
    }

    private boolean findRecipeByName(String name) {
        boolean flag = false;

        for (Recipe recipe : recipeList.values()) {
            if (recipe.getRecipeName().equals(name)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(recipeList);
            filesService.saveToFile(json, dataFileName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        String json = filesService.readFromFile(dataFileName);
        try {
            recipeList = new ObjectMapper().readValue(json, new TypeReference<LinkedHashMap<Long, Recipe>>() {
            });
        } catch (MismatchedInputException e) {
            e.getMessage();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void cleanFile() {
        filesService.cleanDataFile(dataFileName);
    }
}
