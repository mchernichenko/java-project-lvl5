package hexlet.code.service.impl;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    public LabelServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    public List<Label> getAllLabels() {
        return labelRepository.findAll();
    }

    @Override
    public Label getLabelById(Long labelId) {
        return labelRepository.findById(labelId).orElseThrow();
    }

    @Override
    public Label createLabel(LabelDto labelDto) {
        Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
       /* try {
            return labelRepository.save(label);
        } catch (DataIntegrityViolationException ex) {
        throw new NoSuchElementException(((ConstraintViolationException) ex.getCause()).getSQLException().getMessage());
            // ((ConstraintViolationException) ex.cause).sqlException.detailMessage
        }*/
    }

    @Override
    public Label updateLabel(Long labelId, LabelDto labelDto) {
        Label label = labelRepository.findById(labelId).orElseThrow();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public void deleteLabel(Long labelId) {
        labelRepository.deleteById(labelId);
    }
}
