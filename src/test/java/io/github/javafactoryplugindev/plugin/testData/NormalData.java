package io.github.javafactoryplugindev.plugin.testData;


import io.github.javafactoryplugindev.annotation.JavaFactoryData;

@JavaFactoryData(
        referencedData = LowerData.class,
        referencedClass = NormalDataUtlis.class
)
public class NormalData {
}
