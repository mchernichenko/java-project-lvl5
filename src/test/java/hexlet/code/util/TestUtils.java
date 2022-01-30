package hexlet.code.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {
    public static final String AUTH_FIELD = "email";
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private final UserDto testValidUserDto = new UserDto(null, "first", "last", "email@mail.ru", "pwd", null);
    private final TaskStatusDto testStatusDto = new TaskStatusDto(null, "testStatusName", null);

    public UserDto getTestValidUserDto() {
        return testValidUserDto;
    }

    public TaskStatusDto getTestStatusDto() {
        return testStatusDto;
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
