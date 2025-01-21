package com.osrsGoalTracker.hiscore.service.impl;

import com.google.inject.Inject;
import com.osrsGoalTracker.hiscore.external.impl.JagexHiscoresClientImpl;
import com.osrsGoalTracker.hiscore.model.CharacterHiscores;
import com.osrsGoalTracker.hiscore.service.HiscoresService;

import lombok.extern.log4j.Log4j2;

/**
 * Default implementation of the HiscoresService interface.
 * This class implements the domain logic for retrieving and processing
 * character
 * statistics,
 * coordinating between the service layer and data layer.
 */
@Log4j2
public class HiscoresServiceImpl implements HiscoresService {
    private final JagexHiscoresClientImpl jagexHiscoresClient;

    /**
     * Constructs a new DefaultHiscoresService.
     *
     * @param jagexHiscoresClient The client for retrieving character statistics
     */
    @Inject
    public HiscoresServiceImpl(JagexHiscoresClientImpl jagexHiscoresClient) {
        this.jagexHiscoresClient = jagexHiscoresClient;
    }

    @Override
    public CharacterHiscores getCharacterHiscores(String characterName) {
        log.info("Getting character stats for characterName: {}", characterName);
        return jagexHiscoresClient.getCharacterHiscores(characterName);
    }
}
