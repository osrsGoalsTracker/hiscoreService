package com.osrsGoalTracker.hiscore.handler;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrsGoalTracker.hiscore.di.HiscoresModule;
import com.osrsGoalTracker.hiscore.model.CharacterHiscores;
import com.osrsGoalTracker.hiscore.service.HiscoresService;

import lombok.extern.log4j.Log4j2;

/**
 * AWS Lambda handler for retrieving player statistics from OSRS hiscores.
 * This class serves as the entry point for the Lambda function and coordinates
 * the request processing through the application layers.
 */
@Log4j2
public class GetCharacterHiscoresHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int HTTP_OK = 200;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_SERVER_ERROR = 500;

    private final HiscoresService hiscoresService;

    /**
     * Constructs a new GetPlayerStatsHandler.
     * Initializes the dependency injection container and retrieves required
     * services.
     */
    public GetCharacterHiscoresHandler() {
        Injector injector = Guice.createInjector(new HiscoresModule());
        this.hiscoresService = injector.getInstance(HiscoresService.class);
    }

    /**
     * Constructor for testing purposes.
     * Allows injection of mock services in tests.
     *
     * @param statsService The service for retrieving player statistics
     */
    GetCharacterHiscoresHandler(HiscoresService hiscoresService) {
        this.hiscoresService = hiscoresService;
    }

    /**
     * Handles the Lambda request to fetch player statistics.
     *
     * @param input   The API Gateway request event
     * @param context The Lambda execution context
     * @return API Gateway response containing the player's statistics
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            final APIGatewayProxyRequestEvent input,
            final Context context) {
        log.info("Received request to get character hiscores!");
        try {
            String characterName = parseAndValidateInput(input);
            CharacterHiscores hiscores = executeRequest(characterName);
            return createSuccessResponse(hiscores);
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HTTP_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing request", e);
            return createErrorResponse(HTTP_SERVER_ERROR, "Error processing request: " + e.getMessage());
        }
    }

    private String parseAndValidateInput(APIGatewayProxyRequestEvent input) {
        if (input == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        Map<String, String> pathParameters = input.getPathParameters();
        if (pathParameters == null) {
            throw new IllegalArgumentException("Path parameters cannot be null");
        }

        String username = pathParameters.get("name");
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        return username.trim();
    }

    private CharacterHiscores executeRequest(String characterName) throws Exception {
        log.info("Getting character hiscores for characterName: {}", characterName);
        return hiscoresService.getCharacterHiscores(characterName);
    }

    private APIGatewayProxyResponseEvent createSuccessResponse(CharacterHiscores stats) throws Exception {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(HTTP_OK)
                .withBody(OBJECT_MAPPER.writeValueAsString(stats));
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        log.error(message);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(String.format("{\"message\":\"%s\"}", message));
    }
}
