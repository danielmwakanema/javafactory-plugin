package io.github.javafactoryplugindev.plugin.testData;


import io.github.javafactoryplugindev.annotation.JavaFactoryApi;

@JavaFactoryApi(
        referencedData = FooRefData.class,
        defaultFixture = FooRefApiFixture.class,
        defaultImpl =  FooRefImpl.class
)
public interface FooRefApi {
}
