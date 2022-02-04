package hexlet.code.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.TokenService;
import hexlet.code.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.util.TestUtils.AUTH_FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Контроллер реализует следующие end points:
 * GET /api/labels/{id} - получение метки по идентификатору
 * GET /api/labels - получение списка меток
 * POST /api/labels - создание новой метки
 * PUT /api/labels/{id} - обновление метки
 * DELETE /api/labels/{id} - удаление метки
 * Доступы к функциям только авторизованным пользователям
 */

@SpringBootTest
@AutoConfigureMockMvc
@DBRider
@DataSet(value = {"users.yml", "labels.yml"}, cleanAfter = true, transactional = true)
public class LabelControllerTest {

    private static final String BASE_URL = "/api/labels";
    private static final String LOGIN = "mikhail.chernichenko@gmail.com";
    private String userToken;

    @Autowired
    private TestUtils utils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TokenService tokenService;

    @BeforeEach
    public void before() throws Exception {
        userToken = tokenService.expiring(Map.of(AUTH_FIELD, LOGIN));
    }

    @Test
    void createLabel() throws Exception {
        LabelDto newLabelDto = getLabelDto();
        mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(newLabelDto))
                                .contentType(APPLICATION_JSON)
                                .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk());

        var response = mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains(newLabelDto.getName());
    }

    @Test
    void createLabelFail() throws Exception {
        LabelDto newLabelDto = getLabelDto();

        ResultActions resultActions = mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(newLabelDto))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllLabels() throws Exception {
        final var response = mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("label_1", "label_2");
    }

    @Test
    void getLabelById() throws Exception {

        MockHttpServletRequestBuilder request = get(BASE_URL + "/{id}", 1)
                .header(AUTHORIZATION, userToken);

        final var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("label_1");
        assertThat(response.getContentAsString()).doesNotContain("label_2");
    }

    @Test
    void updateLabel() throws Exception {

        final LabelDto updatedLabelDto = LabelDto.builder()
                .name("updateLabel")
                .build();

        MockHttpServletRequestBuilder updateRequest = put(BASE_URL + "/{id}", 1)
                .content(utils.asJson(updatedLabelDto))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, userToken);

        mockMvc.perform(updateRequest)
                .andExpect(status().isOk());

        Label actualLabel = labelRepository.findById(1L).get();
        assertThat(actualLabel).isNotNull();
        assertThat(actualLabel.getName()).isEqualTo("updateLabel");
    }

    @Test
    void deleteLabel() throws Exception {
        mockMvc.perform(
                        delete(BASE_URL + "/{id}", 1)
                                .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(labelRepository.existsById(1L)).isFalse();
    }

    private LabelDto getLabelDto() {
        return LabelDto.builder()
                .name("testLabel")
                .build();
    }
}
