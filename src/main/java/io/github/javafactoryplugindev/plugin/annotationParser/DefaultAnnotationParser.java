package io.github.javafactoryplugindev.plugin.annotationParser;

import io.github.javafactoryplugindev.plugin.PsiClassUtils;
import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryApiParsed;
import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryDataParsed;
import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryPatternParsed;
import com.intellij.psi.*;

import java.util.*;

public class DefaultAnnotationParser implements AnnotationParser {

    private static AnnotationParser INSTANCE = null;

    public static AnnotationParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultAnnotationParser();
        }
        return INSTANCE;
    }


    @Override
    public JavaFactoryDataParsed parseReferencedData(PsiClass dataClass) {
        Set<PsiClass> visited = new HashSet<>();
        Set<PsiClass> referencedData = new LinkedHashSet<>();
        collectReferencedDataRecursive(dataClass, visited, referencedData);

        PsiAnnotation annotation = PsiClassUtils.findAnnotation(dataClass, "JavaFactoryData");

        if (annotation == null) {
            return new JavaFactoryDataParsed(
                    dataClass,
                    List.of(), List.of()
            );
        }
        List<PsiClass> referencedClass = annotation != null
                ? PsiClassUtils.getClassArrayAttribute(annotation, "referencedClass")
                : new ArrayList<>();

        return new JavaFactoryDataParsed(dataClass, new ArrayList<>(referencedData), referencedClass);
    }

    private void collectReferencedDataRecursive(PsiClass target, Set<PsiClass> visited, Set<PsiClass> acc) {
        if (!visited.add(target)) return;

        PsiAnnotation annotation = PsiClassUtils.findAnnotation(target, "JavaFactoryData");
        if (annotation == null) {
            acc.add(target);
            return;
        }

        List<PsiClass> referenced = PsiClassUtils.getClassArrayAttribute(annotation, "referencedData");

        for (PsiClass psi : referenced) {
            if (visited.contains(psi)) continue;
            acc.add(psi);
            collectReferencedDataRecursive(psi, visited, acc);
        }
    }

    @Override
    public JavaFactoryApiParsed parseApiReferences(PsiClass apiInterface) {

        PsiClass selfPsi = apiInterface;

        PsiAnnotation annotation = PsiClassUtils.findAnnotation(apiInterface, "JavaFactoryApi");

        if (annotation == null) {
            throw new MissingAnnotationException(apiInterface, "JavaFactoryApi");
        }

        var defaultImpl = safeGet0(PsiClassUtils.getClassArrayAttribute(annotation, "defaultImpl"));
        var defaultFixture = safeGet0(PsiClassUtils.getClassArrayAttribute(annotation, "defaultFixture"));

        var referencedData = PsiClassUtils.getClassArrayAttribute(annotation, "referencedData");
        var referencedApi = PsiClassUtils.getClassArrayAttribute(annotation, "referencedApi");
        var referencedClass = PsiClassUtils.getClassArrayAttribute(annotation, "referencedClass");

        Set<PsiClass> referencedApiList = new HashSet<>();
        Set<PsiClass> referencedApiImplList = new HashSet<>();
        Set<PsiClass> referencedApiFixtureList = new HashSet<>();

        Set<PsiClass> dataBeforeTraverse = new HashSet<>(referencedData);

        for (var api : referencedApi) {
            PsiAnnotation lowerAnnotation = PsiClassUtils.findAnnotation(api, "JavaFactoryApi");

            referencedApiList.add(api); // 어노테이션 여부 상관없이 포함

            if (lowerAnnotation == null) continue;

            var lowerDefaultImpl = safeGet0(PsiClassUtils.getClassArrayAttribute(lowerAnnotation, "defaultImpl"));
            var lowerDefaultFixture = safeGet0(PsiClassUtils.getClassArrayAttribute(lowerAnnotation, "defaultFixture"));
            var lowerReferencedData = PsiClassUtils.getClassArrayAttribute(lowerAnnotation, "referencedData");

            if (lowerDefaultImpl != null) referencedApiImplList.add(lowerDefaultImpl);
            if (lowerDefaultFixture != null) referencedApiFixtureList.add(lowerDefaultFixture);

            dataBeforeTraverse.addAll(lowerReferencedData);
        }

        Set<PsiClass> finalReferencedDataSet = new HashSet<>();
        for (PsiClass dataClass : dataBeforeTraverse) {
            JavaFactoryDataParsed parsed = parseReferencedData(dataClass);
            finalReferencedDataSet.add(parsed.selfPsi());
            finalReferencedDataSet.addAll(parsed.referencedData());
            finalReferencedDataSet.addAll(parsed.referencedClass());
        }

        return new JavaFactoryApiParsed(
                selfPsi,
                defaultImpl,
                defaultFixture,
                List.copyOf(finalReferencedDataSet),
                List.copyOf(referencedClass),
                List.copyOf(referencedApiList),
                List.copyOf(referencedApiImplList),
                List.copyOf(referencedApiFixtureList)
        );
    }

    private PsiClass safeGet0(List<PsiClass> list) {
        if (list == null || list.size() == 0)
            return null;
        return list.get(0);
    }


    public JavaFactoryPatternParsed parsePattern(PsiClass clazz) {
        PsiAnnotation annotation = PsiClassUtils.findAnnotation(clazz, "JavaFactoryPattern");

        if (annotation == null) {
            throw new MissingAnnotationException(clazz, "JavaFactoryPattern");
        }

        List<String> patternNames = PsiClassUtils.getStringArrayAttribute(annotation, "value");
        return new JavaFactoryPatternParsed(clazz, patternNames);
    }

}
