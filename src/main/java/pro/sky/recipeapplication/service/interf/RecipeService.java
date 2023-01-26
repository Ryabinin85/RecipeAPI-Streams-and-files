package pro.sky.recipeapplication.service.interf;

import pro.sky.recipeapplication.model.Recipe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

public interface RecipeService {
    String getDataFileName();

    void addRecipe(Recipe recipe);

    void addRecipeList(List<Recipe> recipe);

    Recipe getRecipe(Long id);

    String getAllRecipes();

    void editRecipe(long id, Recipe recipe);

    boolean deleteRecipe(long id);

    void deleteAllRecipe();

    List<Recipe> findRecipeByIngredient(long id);

    Path createReport(String recipeName) throws IOException, NoSuchElementException;
}
