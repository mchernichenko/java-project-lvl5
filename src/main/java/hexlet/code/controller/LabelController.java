package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/*
 * Контроллер реализует следующие end points:
 * GET /api/labels/{id} - получение метки по идентификатору
 * GET /api/labels - получение списка меток
 * POST /api/labels - создание новой метки
 * PUT /api/labels/{id} - обновление метки
 * DELETE /api/labels/{id} - удаление метки
 */

@Tag(name = "label", description = "Operations about label")
@RestController
@RequestMapping("${base-url}" + "/labels")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @Operation(summary = "Get all labels", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponse(responseCode = "200", description = "Success")
    @GetMapping(path = "")
    public List<Label> getAllLabels() {
        return labelService.getAllLabels();
    }

    @Operation(summary = "Get label by id", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping(path = "/{id}")
    public Label getLabel(@PathVariable("id") Long labelId) {
        return labelService.getLabelById(labelId);
    }

    @Operation(summary = "Create label", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "422", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public Label createLabel(@RequestBody @Valid LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Update label", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "422", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping(path = "/{id}")
    public Label updateLabel(@PathVariable("id") Long labelId, @RequestBody LabelDto labelDto) {
        return labelService.updateLabel(labelId, labelDto);
    }

    @Operation(summary = "Delete label", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteLabel(@PathVariable("id") Long labelId) {
        labelService.deleteLabel(labelId);
    }
}
