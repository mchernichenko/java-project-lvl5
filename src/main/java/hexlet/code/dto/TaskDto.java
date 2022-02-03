package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    @Size(min = 1, message = "name must be greater than 1 symbol")
    private String name;

    private String description;

    @Min(value = 1, message = "taskStatusId not valid")
    private long taskStatusId;

    private long executorId;

}
