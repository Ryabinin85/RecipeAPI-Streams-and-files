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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.recipeapplication.model.Recipe;
import pro.sky.recipeapplication.service.interf.RecipeService;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/recipe")
@Tag(name = "Рецепты", description = "CRUD операции с рецептами")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping()
    @Operation(summary = "Поиск рецепта по его id")
    public ResponseEntity<Recipe> getRecipe(@RequestParam Long id) {
        Recipe recipe = recipeService.getRecipe(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/findrecipe")
    @Operation(summary = "Поиск рецептов по id ингредиента")
    public ResponseEntity<List<Recipe>> findRecipeByIngredient(@RequestParam Long id) {
        if (recipeService.findRecipeByIngredient(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipeService.findRecipeByIngredient(id));
    }

    @GetMapping("/all")
    @Operation(summary = "Получение всех рецептов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепты получены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Recipe.class))
                            )
                    }
            )
    })
    public String getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @PostMapping("/add")
    @Operation(summary = "Добавление нового рецепта")
    public ResponseEntity<Recipe> addRecipe(@RequestBody Recipe recipe) {
        recipeService.addRecipe(recipe);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addlist")
    @Operation(summary = "Добавление списка рецептов")
    public ResponseEntity<List<Recipe>> addRecipeList(@RequestBody List<Recipe> recipe) {
        if (recipe.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            recipeService.addRecipeList(recipe);
            return ResponseEntity.ok(recipe);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменение рецепта",
            description = "Поиск рецепта по его id и изменение значений его полей")
    @Parameters(value = {
            @Parameter(name = "id", description = "id рецепта"),
            @Parameter(name = "recipe", description = "рецепт")
    })
    public ResponseEntity<Recipe> editIngredient(@PathVariable long id, @RequestBody Recipe recipe) {
        recipeService.editRecipe(id, recipe);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление рецепта",
            description = "Удаление рецепта по его id")
    @Parameter(name = "id", description = "id рецепта")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        if (recipeService.deleteRecipe(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    @Operation(summary = "Удаление всех рецептов")
    public ResponseEntity<Void> deleteAllRecipe() {
        recipeService.deleteAllRecipe();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report")
    public ResponseEntity<Object> downloadReport(@RequestParam String recipeName) {
        try {
            Path path = recipeService.createReport(recipeName);
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.txt\"")
                    .contentLength(Files.size(path))
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }
}
