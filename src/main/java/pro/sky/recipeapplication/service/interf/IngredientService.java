package pro.sky.recipeapplication.service.interf;

import pro.sky.recipeapplication.model.Ingredient;

import java.util.List;

public interface IngredientService {
    Ingredient getIngredient(Long identifier);


    void addNewIngredient(Ingredient ingredient);

    String getAllIngredients();

    void editIngredient(long id, Ingredient ingredient);

    boolean deleteIngredient(long id);

    void deleteAllIngredients();

    String getDataFileName();

    void addIngredientFromRecipe(List<Ingredient> ingredients);
}
