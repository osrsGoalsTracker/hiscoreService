package com.osrsGoalTracker.hiscore.model;

import lombok.Builder;
import lombok.Value;

/**
 * Model representing a RuneScape activity.
 */
@Value
@Builder
public class Activity {
    /**
     * The name of the activity.
     */
    private final String name;

    /**
     * The player's rank in this activity.
     */
    private final int rank;

    /**
     * The player's score in this activity.
     */
    private final int score;
}