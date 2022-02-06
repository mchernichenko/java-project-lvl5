package hexlet.code;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Task Manager OpenAPI definition",
                version = "1.0",
                description = " API для работы с пользователями, задачами и справочниками: "
                        + "просмотр задач, фильтрация задач, назначение задач пользователям,"
                        + " изменение и удаление задач.\n"
                        + "CRUD операции для атрибутов задач: пользователи, статусы, метки.",
                contact = @Contact(
                        name = "Mikhail Chernichenko",
                        email = "mchernichenko@mail.ru"
                )
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SpringBootApplication
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
