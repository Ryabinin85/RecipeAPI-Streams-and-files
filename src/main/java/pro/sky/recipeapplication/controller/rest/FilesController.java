package pro.sky.recipeapplication.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.recipeapplication.service.interf.FilesService;
import pro.sky.recipeapplication.service.interf.IngredientService;
import pro.sky.recipeapplication.service.interf.RecipeService;

import java.io.*;

@RestController
@RequestMapping("/files")
@Tag(name = "Контроллер работы с файлами", description = "Загрузка и выгрузка файлов с сервера")
public class FilesController {
    private final FilesService filesService;
    private final IngredientService ingredients;
    private final RecipeService recipes;


    public FilesController(FilesService filesService,
                           IngredientService ingredients,
                           RecipeService recipes) {
        this.filesService = filesService;
        this.ingredients = ingredients;
        this.recipes = recipes;
    }

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Загрузка файла с рецептами с сервера")
    public ResponseEntity<InputStreamResource> downloadDataFile() throws FileNotFoundException {
        File dataFile = filesService.getDataFile(recipes.getDataFileName());

        if (dataFile.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(dataFile));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"_recipes.json\"")
                    .contentLength(dataFile.length())
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/recipesimport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка файла с рецептами на сервер")
    public ResponseEntity<Void> uploadRecipesDataFile(@RequestParam MultipartFile file) {

        return filesResponseEntity(file, recipes.getDataFileName());
    }


    @PostMapping(value = "/ingredientsimport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка файла с ингредиентами на сервер")
    public ResponseEntity<Void> uploadIngredientsFile(@RequestParam MultipartFile file) {

        return filesResponseEntity(file, ingredients.getDataFileName());
    }

    @NotNull
    private ResponseEntity<Void> filesResponseEntity(MultipartFile file, String dataFileName) {
        filesService.cleanDataFile(dataFileName);
        File dataFile = filesService.getDataFile(dataFileName);

        try (FileOutputStream fos = new FileOutputStream(dataFile)) {
            IOUtils.copy(file.getInputStream(), fos);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
