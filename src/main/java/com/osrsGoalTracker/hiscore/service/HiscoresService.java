package com.osrsGoalTracker.hiscore.service;

import com.osrsGoalTracker.hiscore.model.CharacterHiscores;

/**
 * Domain logic service interface for retrieving OSRS character stats.
 */
public interface HiscoresService {
    /**
     * Retrieves stats for a character from the OSRS hiscores.
     *
     * @param characterName The RuneScape name of the character
     * @return The character's stats
     * @throws RuntimeException if there's an error fetching the stats
     */
    CharacterHiscores getCharacterHiscores(String characterName);
}
