package com.bluecc.refs.ecommerce;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class AppModule {

    private static final class InjectorHolder {
        static final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
            }
        });
    }

    public static <T> T getInstance(Class<T> clz){
        return InjectorHolder.injector.getInstance(clz);
    }
}
