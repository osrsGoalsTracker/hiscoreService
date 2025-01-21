package com.osrsGoalTracker.hiscore.di;

import com.google.inject.AbstractModule;
import com.osrsGoalTracker.hiscore.external.HiscoresClient;
import com.osrsGoalTracker.hiscore.external.impl.JagexHiscoresClientImpl;
import com.osrsGoalTracker.hiscore.service.HiscoresService;
import com.osrsGoalTracker.hiscore.service.impl.HiscoresServiceImpl;

/**
 * Guice module for hiscores-related bindings.
 */
public class HiscoresModule extends AbstractModule {
    /**
     * Configures the bindings for stats-related services and dependencies.
     * This includes the service layer and external layer components.
     */
    @Override
    protected void configure() {
        bind(HiscoresClient.class).to(JagexHiscoresClientImpl.class);
        bind(HiscoresService.class).to(HiscoresServiceImpl.class);
    }
}
