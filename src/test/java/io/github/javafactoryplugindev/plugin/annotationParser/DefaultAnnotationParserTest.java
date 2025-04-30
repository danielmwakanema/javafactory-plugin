package io.github.javafactoryplugindev.plugin.annotationParser;

import io.github.javafactoryplugindev.plugin.LoadSamplesUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;


import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;


import java.io.IOException;

public class DefaultAnnotationParserTest extends BasePlatformTestCase {

    private final AnnotationParser parser = DefaultAnnotationParser.getInstance();


    public void test_parse_api_info_all_data_parsed() throws IOException {

        LoadSamplesUtil.loadTestSources(myFixture);

        Project project = getProject();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        PsiClass psiClass = facade.findClass("com.github.javafactorydev.plugin.testData.FooApi", scope);

        assertNotNull("FooApi 클래스를 찾아야 한다", psiClass);

        var parsed = parser.parseApiReferences(psiClass);

        assertNotNull(parsed.defaultImpl());
        assertNotNull(parsed.defaultFixture());

        assertFalse(parsed.referencedApi().isEmpty());
        assertFalse(parsed.referencedData().isEmpty());
        assertFalse(parsed.referencedApiFixture().isEmpty());

        assertFalse(parsed.referencedClass().isEmpty());

        assertEquals("FooApiImpl", parsed.defaultImpl().getName());
    }


    public void test_parse_data_info_all_field_parsed() throws IOException {

        LoadSamplesUtil.loadTestSources(myFixture);

        Project project = getProject();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        PsiClass psiClass = facade.findClass("com.github.javafactorydev.plugin.testData.NormalData", scope);

        assertNotNull("NormalData 클래스를 찾아야 한다", psiClass);

        var parsed = parser.parseReferencedData(psiClass);

        assertNotNull(parsed);

        assertEquals(1, parsed.referencedData().size());
        assertEquals(1, parsed.referencedClass().size());

    }

    public void test_parse_data_info_recursive_not_infinite() throws IOException {

        LoadSamplesUtil.loadTestSources(myFixture);

        Project project = getProject();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        PsiClass psiClass = facade.findClass("com.github.javafactorydev.plugin.testData.RecursiveDataA", scope);

        assertNotNull("RecursiveDataA 클래스를 찾아야 한다", psiClass);

        var parsed = parser.parseReferencedData(psiClass);

        assertNotNull(parsed);
        assertFalse(parsed.referencedData().size() == 2);

    }

    public void test_parse_annotation() throws IOException {
        LoadSamplesUtil.loadTestSources(myFixture);

        Project project = getProject();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        PsiClass psiClass = facade.findClass("com.github.javafactorydev.plugin.testData.NoAnnotation", scope);

        assertNotNull("NoAnnotation 클래스를 찾아야 한다", psiClass);

        var dataParsed = parser.parseReferencedData(psiClass);
        assertNotNull(dataParsed.selfPsi());

        assertThrows(MissingAnnotationException.class,
                ()->parser.parseApiReferences(psiClass)
                );

        assertThrows(MissingAnnotationException.class, ()->parser.parsePattern(psiClass));

    }


    public void test_parse_pattern_all_pattern_resolved() throws IOException {

        LoadSamplesUtil.loadTestSources(myFixture);

        Project project = getProject();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        PsiClass psiClass = facade.findClass("com.github.javafactorydev.plugin.testData.FooApi", scope);

        assertNotNull("FooApi 클래스를 찾아야 한다", psiClass);

        var parsed = parser.parsePattern(psiClass);


        assertNotNull(parsed);
        assertEquals(2, parsed.patternNames().size());
        assertTrue(parsed.patternNames().contains("FooPattern"));
        assertTrue(parsed.patternNames().contains("BarPattern"));
    }


}