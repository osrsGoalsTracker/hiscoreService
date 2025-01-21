package com.osrsGoalTracker.hiscore.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osrsGoalTracker.hiscore.model.Activity;
import com.osrsGoalTracker.hiscore.model.Skill;
import com.osrsGoalTracker.hiscore.model.CharacterHiscores;
import com.osrsGoalTracker.hiscore.service.HiscoresService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetCharacterHiscoresHandlerTest {

    @Mock
    private HiscoresService hiscoresService;

    @Mock
    private Context context;

    private GetCharacterHiscoresHandler handler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        handler = new GetCharacterHiscoresHandler(hiscoresService);
        objectMapper = new ObjectMapper();
    }

    @Test
    void handleRequest_ValidInput_ReturnsSuccessResponse() throws Exception {
        // Given
        String characterName = "TestCharacter";
        List<Skill> skills = Arrays.asList(
                Skill.builder()
                        .name("Attack")
                        .rank(100000)
                        .level(99)
                        .xp(13034431)
                        .build());
        List<Activity> activities = Arrays.asList(
                Activity.builder()
                        .name("Clue Scrolls (all)")
                        .rank(50000)
                        .score(100)
                        .build());
        CharacterHiscores expectedHiscores = CharacterHiscores.builder()
                .characterName(characterName)
                .skills(skills)
                .activities(activities)
                .build();

        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("name", characterName);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters);

        when(hiscoresService.getCharacterHiscores(characterName)).thenReturn(expectedHiscores);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(expectedHiscores), response.getBody());
        verify(hiscoresService).getCharacterHiscores(characterName);
    }

    @Test
    void handleRequest_NullInput_ReturnsBadRequest() {
        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(null, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Request cannot be null\"}", response.getBody());
    }

    @Test
    void handleRequest_NullPathParameters_ReturnsBadRequest() {
        // Given
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Path parameters cannot be null\"}", response.getBody());
    }

    @Test
    void handleRequest_MissingName_ReturnsBadRequest() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Name cannot be null or empty\"}", response.getBody());
    }

    @Test
    void handleRequest_EmptyName_ReturnsBadRequest() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("name", "");
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Name cannot be null or empty\"}", response.getBody());
    }

    @Test
    void handleRequest_ServiceThrowsException_ReturnsServerError() throws Exception {
        // Given
        String characterName = "TestCharacter";
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("name", characterName);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters);

        RuntimeException expectedException = new RuntimeException("Service error");
        when(hiscoresService.getCharacterHiscores(characterName)).thenThrow(expectedException);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
        assertEquals("{\"message\":\"Error processing request: Service error\"}", response.getBody());
    }
}