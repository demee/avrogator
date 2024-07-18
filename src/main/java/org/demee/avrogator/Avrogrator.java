package org.demee.avrogator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.demee.avrogator.di.AvrogatorAppModule;

public class Avrogrator {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AvrogatorAppModule());
        injector.getInstance(AvrogatorApplication.class).launchApplication(args);
    }
}
