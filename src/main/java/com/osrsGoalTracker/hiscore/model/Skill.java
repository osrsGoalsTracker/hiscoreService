package com.osrsGoalTracker.hiscore.model;

import lombok.Builder;
import lombok.Value;

/**
 * Model representing a RuneScape skill.
 */
@Value
@Builder
public class Skill {
    /**
     * The name of the skill.
     */
    private final String name;

    /**
     * The player's rank in this skill.
     */
    private final int rank;

    /**
     * The player's level in this skill.
     */
    private final int level;

    /**
     * The player's experience points in this skill.
     */
    private final long xp;
}