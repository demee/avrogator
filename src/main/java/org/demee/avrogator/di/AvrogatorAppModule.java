package org.demee.avrogator.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.demee.avrogator.AvroParser;
import org.demee.avrogator.AvrogatorController;

public class AvrogatorAppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AvrogatorController.class).asEagerSingleton();
    }
    @Provides
    public AvroParser provideAvroParser() {
        return new AvroParser();
    }
}
