package com.osrsGoalTracker.hiscore.model;

import java.util.List;

import lombok.Builder;
import lombok.Value;

/**
 * Model representing a character's hiscores.
 */
@Value
@Builder
public class CharacterHiscores {
    /**
     * The name of the character.
     */
    private final String characterName;

    /**
     * The list of skills.
     */
    private final List<Skill> skills;

    /**
     * The list of activities.
     */
    private final List<Activity> activities;
}