package com.osrsGoalTracker.hiscore.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class CharacterHiscoresTest {

    @Test
    void testCharacterHiscoresBuilder() {
        // Given
        String characterName = "TestCharacter";
        List<Skill> skills = Arrays.asList(
                Skill.builder()
                        .name("Attack")
                        .rank(100000)
                        .level(99)
                        .xp(13034431)
                        .build(),
                Skill.builder()
                        .name("Defence")
                        .rank(90000)
                        .level(99)
                        .xp(13034431)
                        .build());
        List<Activity> activities = Arrays.asList(
                Activity.builder()
                        .name("Clue Scrolls (all)")
                        .rank(50000)
                        .score(100)
                        .build(),
                Activity.builder()
                        .name("Clue Scrolls (hard)")
                        .rank(40000)
                        .score(50)
                        .build());

        // When
        CharacterHiscores hiscores = CharacterHiscores.builder()
                .characterName(characterName)
                .skills(skills)
                .activities(activities)
                .build();

        // Then
        assertEquals(characterName, hiscores.getCharacterName());
        assertEquals(skills, hiscores.getSkills());
        assertEquals(activities, hiscores.getActivities());
        assertEquals(2, hiscores.getSkills().size());
        assertEquals(2, hiscores.getActivities().size());
    }

    @Test
    void testCharacterHiscoresEquality() {
        // Given
        List<Skill> skills1 = Arrays.asList(
                Skill.builder()
                        .name("Attack")
                        .rank(100000)
                        .level(99)
                        .xp(13034431)
                        .build());
        List<Activity> activities1 = Arrays.asList(
                Activity.builder()
                        .name("Clue Scrolls (all)")
                        .rank(50000)
                        .score(100)
                        .build());

        CharacterHiscores hiscores1 = CharacterHiscores.builder()
                .characterName("TestCharacter")
                .skills(skills1)
                .activities(activities1)
                .build();

        CharacterHiscores hiscores2 = CharacterHiscores.builder()
                .characterName("TestCharacter")
                .skills(skills1)
                .activities(activities1)
                .build();

        CharacterHiscores differentHiscores = CharacterHiscores.builder()
                .characterName("DifferentCharacter")
                .skills(skills1)
                .activities(activities1)
                .build();

        // Then
        assertEquals(hiscores1, hiscores2, "Two hiscores with the same values should be equal");
        assertNotEquals(hiscores1, differentHiscores, "Hiscores with different values should not be equal");
        assertEquals(hiscores1.hashCode(), hiscores2.hashCode(), "Equal hiscores should have the same hash code");
    }

    @Test
    void testCharacterHiscoresNullLists() {
        // When
        CharacterHiscores hiscores = CharacterHiscores.builder()
                .characterName("TestCharacter")
                .build();

        // Then
        assertNotNull(hiscores);
        assertEquals("TestCharacter", hiscores.getCharacterName());
        assertNull(hiscores.getSkills(), "Skills list should be null when not set");
        assertNull(hiscores.getActivities(), "Activities list should be null when not set");
    }
}