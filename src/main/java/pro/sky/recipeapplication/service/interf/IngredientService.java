package pro.sky.recipeapplication.service.interf;

import pro.sky.recipeapplication.model.Ingredient;

import java.util.List;
import java.util.Map;

public interface IngredientService {
    Ingredient getIngredient(Long identifier);


    boolean addNewIngredient(Ingredient ingredient);

    Map<Long, Ingredient> getAllIngredients();

    boolean editIngredient(long id, Ingredient ingredient);

    boolean deleteIngredient(long id);

    void deleteAllIngredients();

    String getDataFileName();

    void addIngredientFromRecipe(List<Ingredient> ingredients);
}
