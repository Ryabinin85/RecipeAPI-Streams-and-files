package pro.sky.recipeapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Positive
    private int quantity;

    @NotBlank(message = "Units is mandatory")
    private String measureUnit;

    @Override
    public String toString() {
        return name + " - " + quantity + " - " + measureUnit;
    }
}
