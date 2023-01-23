package pro.sky.recipeapplication.service.interf;

import pro.sky.recipeapplication.model.Recipe;

import java.util.List;

public interface RecipeService {
    void addRecipe(Recipe recipe);

    void addRecipeList(List<Recipe> recipe);

    Recipe getRecipe(Long id);

    String getAllRecipes();

    void editRecipe(long id, Recipe recipe);

    boolean deleteRecipe(long id);

    void deleteAllRecipe();

    List<Recipe> findRecipeByIngredient(long id);
}
