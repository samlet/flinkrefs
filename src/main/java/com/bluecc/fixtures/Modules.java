package com.bluecc.fixtures;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Modules {
    public static Injector build(){
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
            }
        });
    }
}
