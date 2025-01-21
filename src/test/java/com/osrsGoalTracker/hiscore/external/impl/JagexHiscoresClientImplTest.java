package com.osrsGoalTracker.hiscore.external.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.osrsGoalTracker.hiscore.model.Activity;
import com.osrsGoalTracker.hiscore.model.Skill;
import com.osrsGoalTracker.hiscore.model.CharacterHiscores;
import com.osrshiscores.apiclient.OsrsApiClient;
import com.osrshiscores.apiclient.model.output.OsrsPlayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JagexHiscoresClientImplTest {

    @Mock
    private OsrsApiClient osrsApiClient;

    private JagexHiscoresClientImpl hiscoresClient;

    private OsrsPlayer mockOsrsPlayer;

    @BeforeEach
    void setUp() {
        mockOsrsPlayer = mock(OsrsPlayer.class);
        hiscoresClient = new JagexHiscoresClientImpl() {
            @Override
            protected OsrsApiClient createOsrsApiClient() {
                return osrsApiClient;
            }
        };
    }

    @Test
    void getCharacterHiscores_ValidCharacter_ReturnsHiscores() throws IOException {
        // Given
        String characterName = "TestCharacter";
        List<com.osrshiscores.apiclient.model.output.Skill> osrsSkills = Arrays.asList(
                createOsrsSkill("Attack", 100000, 99, 13034431),
                createOsrsSkill("Defence", 90000, 99, 13034431));
        List<com.osrshiscores.apiclient.model.output.Activity> osrsActivities = Arrays.asList(
                createOsrsActivity("Clue Scrolls (all)", 50000, 100),
                createOsrsActivity("Clue Scrolls (hard)", 40000, 50));

        when(osrsApiClient.getPlayerByRsn(characterName)).thenReturn(mockOsrsPlayer);
        when(mockOsrsPlayer.getSkills()).thenReturn(osrsSkills);
        when(mockOsrsPlayer.getActivities()).thenReturn(osrsActivities);

        // When
        CharacterHiscores hiscores = hiscoresClient.getCharacterHiscores(characterName);

        // Then
        assertNotNull(hiscores);
        assertEquals(characterName, hiscores.getCharacterName());
        assertEquals(2, hiscores.getSkills().size());
        assertEquals(2, hiscores.getActivities().size());

        // Verify skills
        List<Skill> skills = hiscores.getSkills();
        assertEquals("Attack", skills.get(0).getName());
        assertEquals(100000, skills.get(0).getRank());
        assertEquals(99, skills.get(0).getLevel());
        assertEquals(13034431, skills.get(0).getXp());

        assertEquals("Defence", skills.get(1).getName());
        assertEquals(90000, skills.get(1).getRank());
        assertEquals(99, skills.get(1).getLevel());
        assertEquals(13034431, skills.get(1).getXp());

        // Verify activities
        List<Activity> activities = hiscores.getActivities();
        assertEquals("Clue Scrolls (all)", activities.get(0).getName());
        assertEquals(50000, activities.get(0).getRank());
        assertEquals(100, activities.get(0).getScore());

        assertEquals("Clue Scrolls (hard)", activities.get(1).getName());
        assertEquals(40000, activities.get(1).getRank());
        assertEquals(50, activities.get(1).getScore());
    }

    @Test
    void getCharacterHiscores_ApiError_ThrowsRuntimeException() throws IOException {
        // Given
        when(osrsApiClient.getPlayerByRsn(anyString())).thenThrow(new IOException("API Error"));

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> hiscoresClient.getCharacterHiscores("TestCharacter"));
        assertEquals("Error fetching character hiscores", exception.getMessage());
    }

    private com.osrshiscores.apiclient.model.output.Skill createOsrsSkill(String name, int rank, int level, long xp) {
        com.osrshiscores.apiclient.model.output.Skill skill = mock(com.osrshiscores.apiclient.model.output.Skill.class);
        when(skill.getName()).thenReturn(name);
        when(skill.getRank()).thenReturn(rank);
        when(skill.getLevel()).thenReturn(level);
        when(skill.getXp()).thenReturn(xp);
        return skill;
    }

    private com.osrshiscores.apiclient.model.output.Activity createOsrsActivity(String name, int rank, int score) {
        com.osrshiscores.apiclient.model.output.Activity activity = mock(
                com.osrshiscores.apiclient.model.output.Activity.class);
        when(activity.getName()).thenReturn(name);
        when(activity.getRank()).thenReturn(rank);
        when(activity.getScore()).thenReturn(score);
        return activity;
    }
}