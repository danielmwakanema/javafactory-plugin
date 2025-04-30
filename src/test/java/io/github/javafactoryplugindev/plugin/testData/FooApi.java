package io.github.javafactoryplugindev.plugin.testData;


import io.github.javafactoryplugindev.annotation.JavaFactoryApi;
import io.github.javafactoryplugindev.annotation.JavaFactoryPattern;

@JavaFactoryApi(
        defaultImpl = FooApiImpl.class,
        referencedApi = FooRefApi.class,
        referencedData = Foo.class,
        defaultFixture = FooApiFixture.class,
        referencedClass = FooReferencedClass.class
)
@JavaFactoryPattern(value = {"FooPattern", "BarPattern"})
public interface FooApi {
}
