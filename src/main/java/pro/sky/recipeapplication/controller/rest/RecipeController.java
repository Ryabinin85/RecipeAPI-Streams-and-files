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
import java.util.Map;

@RestController
@RequestMapping("/recipe")
@Tag(name = "Рецепты", description = "CRUD операции с рецептами")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Поиск рецепта по его id")
    public ResponseEntity<Recipe> getRecipe(@RequestParam Long id) {
        Recipe recipe = recipeService.getRecipe(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @GetMapping(value = "/findrecipe", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Поиск рецептов по id ингредиента")
    public ResponseEntity<List<Recipe>> findRecipeByIngredient(@RequestParam Long id) {
        if (recipeService.findRecipeByIngredient(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipeService.findRecipeByIngredient(id));
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
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
    public ResponseEntity<Map<Long, Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Добавление нового рецепта")
    public ResponseEntity<Recipe> addRecipe(@RequestBody Recipe recipe) {
        if (recipeService.addRecipe(recipe)) {
            return ResponseEntity.ok().build();
        } else return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/addlist", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Добавление списка рецептов")
    public ResponseEntity<List<Recipe>> addRecipeList(@RequestBody List<Recipe> recipe) {
        if (recipe.isEmpty() || !recipeService.addRecipeList(recipe)) {
            return ResponseEntity.badRequest().build();
        } else {
            recipeService.addRecipeList(recipe);
            return ResponseEntity.ok(recipe);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = "/report", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Скачать рецепт")
    @Parameter(name = "recipeName", description = "Ввести название рецепта")
    public ResponseEntity<Object> downloadReport(@RequestParam String recipeName) {

        try {
            if (recipeService.createReport(recipeName) == null) {
                return ResponseEntity.notFound().build();
            }
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
