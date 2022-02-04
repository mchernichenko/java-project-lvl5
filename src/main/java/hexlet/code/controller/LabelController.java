package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер реализует следующие end points:
 * GET /api/labels/{id} - получение метки по идентификатору
 * GET /api/labels - получение списка меток
 * POST /api/labels - создание новой метки
 * PUT /api/labels/{id} - обновление метки
 * DELETE /api/labels/{id} - удаление метки
 */

@RestController
@RequestMapping("${base-url}" + "/labels")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @GetMapping(path = "")
    public List<Label> getAllLabels() {
        return labelService.getAllLabels();
    }

    @GetMapping(path = "/{id}")
    public Label getLabel(@PathVariable("id") Long labelId) {
        return labelService.getLabelById(labelId);
    }

    @PostMapping(path = "")
    public Label createLabel(@RequestBody @Valid LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @PutMapping(path = "/{id}")
    public Label updateLabel(@PathVariable("id") Long labelId, @RequestBody LabelDto labelDto) {
        return labelService.updateLabel(labelId, labelDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteLabel(@PathVariable("id") Long labelId) {
        labelService.deleteLabel(labelId);
    }
}
