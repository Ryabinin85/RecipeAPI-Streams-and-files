package pro.sky.recipeapplication.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.recipeapplication.model.Ingredient;
import pro.sky.recipeapplication.service.interf.IngredientService;

@RestController
@RequestMapping("/ingredients")
@Tag(name = "Ингредиенты", description = "CRUD операции с ингредиентами")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping()
    @Operation(summary = "Поиск ингредиента по его id")
    @Parameter(name = "id", description = "id ингредиента")
    public ResponseEntity<Ingredient> getIngredient(@RequestParam Long id) {
        if (ingredientService.getIngredient(id) == null) {
            return ResponseEntity.notFound().build();
        } else
            return ResponseEntity.ok(ingredientService.getIngredient(id));
    }

    @GetMapping("/all")
    @Operation(summary = "Получение всех ингредиентов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ингредиенты получены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Ingredient.class))
                            )
                    }
            )
    })
    public String getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @PostMapping("/add")
    @Operation(summary = "Добавление нового ингредиента")
    public ResponseEntity<Ingredient> addIngredient(@RequestBody Ingredient ingredient) {
        ingredientService.addNewIngredient(ingredient);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменение ингредиента",
            description = "Поиск ингредиента по его id и изменение значений его полей")
    @Parameters(value = {
            @Parameter(name = "id", description = "id ингредиента"),
            @Parameter(name = "ingredient", description = "ингредиент")
    })
    public ResponseEntity<Ingredient> editIngredient(@PathVariable long id, @RequestBody Ingredient ingredient) {
        ingredientService.editIngredient(id, ingredient);
        if (ingredient == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ingredient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление ингредиента",
            description = "Удаление ингредиента по его id")
    @Parameter(name = "id", description = "id ингредиента")
    public ResponseEntity<Void> deleteIngredient(@PathVariable long id) {
        if (ingredientService.deleteIngredient(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    @Operation(summary = "Удаление всех ингредиентов")
    public ResponseEntity<Void> deleteAllIngredient() {
        ingredientService.deleteAllIngredients();
        return ResponseEntity.ok().build();
    }
}
