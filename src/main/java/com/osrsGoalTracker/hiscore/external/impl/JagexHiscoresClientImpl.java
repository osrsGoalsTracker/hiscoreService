package com.osrsGoalTracker.hiscore.external.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.osrsGoalTracker.hiscore.model.Activity;
import com.osrsGoalTracker.hiscore.model.Skill;
import com.osrsGoalTracker.hiscore.external.HiscoresClient;
import com.osrsGoalTracker.hiscore.model.CharacterHiscores;
import com.osrshiscores.apiclient.OsrsApiClient;
import com.osrshiscores.apiclient.model.output.OsrsPlayer;

import lombok.extern.log4j.Log4j2;

/**
 * Implementation of the HiscoresClient interface that uses the Jagex OSRS API.
 */
@Log4j2
public class JagexHiscoresClientImpl implements HiscoresClient {
    private final OsrsApiClient osrsApiClient;

    /**
     * Constructs a new HiscoresClient.
     * Initializes the OSRS API client for making hiscores requests.
     */
    @Inject
    public JagexHiscoresClientImpl() {
        this.osrsApiClient = createOsrsApiClient();
    }

    /**
     * Creates a new OsrsApiClient instance.
     * This method can be overridden in tests to provide a mock client.
     *
     * @return A new OsrsApiClient instance
     */
    protected OsrsApiClient createOsrsApiClient() {
        return new OsrsApiClient();
    }

    @Override
    public CharacterHiscores getCharacterHiscores(String characterName) {
        try {
            OsrsPlayer player = osrsApiClient.getPlayerByRsn(characterName);
            return CharacterHiscores.builder()
                    .characterName(characterName)
                    .skills(convertSkills(player.getSkills()))
                    .activities(convertActivities(player.getActivities()))
                    .build();
        } catch (IOException e) {
            log.error("Failed to fetch character hiscores for {}: {}", characterName, e.getMessage());
            throw new RuntimeException("Error fetching character hiscores", e);
        }
    }

    private List<Skill> convertSkills(List<com.osrshiscores.apiclient.model.output.Skill> skills) {
        return skills.stream()
                .map(skill -> Skill.builder()
                        .name(skill.getName())
                        .rank(skill.getRank())
                        .level(skill.getLevel())
                        .xp(skill.getXp())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Activity> convertActivities(List<com.osrshiscores.apiclient.model.output.Activity> activities) {
        return activities.stream()
                .map(activity -> Activity.builder()
                        .name(activity.getName())
                        .rank(activity.getRank())
                        .score(activity.getScore())
                        .build())
                .collect(Collectors.toList());
    }
}
