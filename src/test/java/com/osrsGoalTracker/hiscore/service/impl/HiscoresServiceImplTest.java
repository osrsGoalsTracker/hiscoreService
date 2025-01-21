package com.osrsGoalTracker.hiscore.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.osrsGoalTracker.hiscore.external.impl.JagexHiscoresClientImpl;
import com.osrsGoalTracker.hiscore.model.CharacterHiscores;
import com.osrsGoalTracker.hiscore.model.Activity;
import com.osrsGoalTracker.hiscore.model.Skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HiscoresServiceImplTest {

    @Mock
    private JagexHiscoresClientImpl jagexHiscoresClient;

    private HiscoresServiceImpl hiscoresService;

    @BeforeEach
    void setUp() {
        hiscoresService = new HiscoresServiceImpl(jagexHiscoresClient);
    }

    @Test
    void getCharacterHiscores_ValidCharacter_ReturnsHiscores() {
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

        when(jagexHiscoresClient.getCharacterHiscores(characterName)).thenReturn(expectedHiscores);

        // When
        CharacterHiscores actualHiscores = hiscoresService.getCharacterHiscores(characterName);

        // Then
        assertEquals(expectedHiscores, actualHiscores);
        verify(jagexHiscoresClient).getCharacterHiscores(characterName);
    }

    @Test
    void getCharacterHiscores_ClientThrowsException_PropagatesException() {
        // Given
        String characterName = "TestCharacter";
        RuntimeException expectedException = new RuntimeException("API Error");
        when(jagexHiscoresClient.getCharacterHiscores(characterName)).thenThrow(expectedException);

        // When/Then
        RuntimeException actualException = assertThrows(RuntimeException.class,
                () -> hiscoresService.getCharacterHiscores(characterName));
        assertEquals(expectedException, actualException);
    }
}