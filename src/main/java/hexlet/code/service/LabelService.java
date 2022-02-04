package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {
    List<Label> getAllLabels();

    Label getLabelById(Long labelId);

    Label createLabel(LabelDto labelDto);

    Label updateLabel(Long labelId, LabelDto labelDto);

    void deleteLabel(Long labelId);
}
