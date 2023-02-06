package pro.sky.recipeapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

    @NotBlank(message = "Name is mandatory")
    private String recipeName;

    @Positive
    private int cookTime;

    private List<Ingredient> ingredients;

    private Map<Long, String> cookingInstruction;
}
