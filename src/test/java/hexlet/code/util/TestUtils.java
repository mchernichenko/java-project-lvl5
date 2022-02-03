package hexlet.code.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {
    public static final String AUTH_FIELD = "email";
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskStatusRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    private final UserDto testValidUserDto = UserDto.builder()
            .firstName("first")
            .lastName("last")
            .email("email@mail.ru")
            .password("pwd")
            .build();

    public void tearDown() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    public UserDto getTestValidUserDto() {
        return testValidUserDto;
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
