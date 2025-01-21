package com.osrsGoalTracker.hiscore.di;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrsGoalTracker.hiscore.external.HiscoresClient;
import com.osrsGoalTracker.hiscore.external.impl.JagexHiscoresClientImpl;
import com.osrsGoalTracker.hiscore.service.HiscoresService;
import com.osrsGoalTracker.hiscore.service.impl.HiscoresServiceImpl;

import org.junit.jupiter.api.Test;

class HiscoresModuleTest {

    @Test
    void testHiscoresModuleBindings() {
        // Given
        Injector injector = Guice.createInjector(new HiscoresModule());

        // When
        HiscoresClient hiscoresClient = injector.getInstance(HiscoresClient.class);
        HiscoresService hiscoresService = injector.getInstance(HiscoresService.class);

        // Then
        assertNotNull(hiscoresClient, "HiscoresClient should be bound");
        assertNotNull(hiscoresService, "HiscoresService should be bound");
        assertTrue(hiscoresClient instanceof JagexHiscoresClientImpl,
                "HiscoresClient should be bound to JagexHiscoresClientImpl");
        assertTrue(hiscoresService instanceof HiscoresServiceImpl,
                "HiscoresService should be bound to HiscoresServiceImpl");
    }
}