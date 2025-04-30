package io.github.javafactoryplugindev.plugin.annotationParser;

import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryApiParsed;
import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryDataParsed;
import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryPatternParsed;
import com.intellij.psi.PsiClass;

public interface AnnotationParser {


    /**
     * # JOB
     * 해당 클래스가 @JavaFactoryData인 경우, 데이터 참조만 추출
     * 순환 탐색하여, Data 하위의 Data 까지 수집
     *
     *  # SIDE CASE
     * 어노테이션이 없는 경우 - 해당 클래스 정보만 리턴, 순환 탐색 안함
     */
    JavaFactoryDataParsed parseReferencedData(PsiClass dataClass);

    /**
     *  # JOB
     * 해당 클래스가 @JavaFactoryApi인 경우, API 관련 참조 정보만 추출
     * (기본 구현체, fixture, referencedApi, 등등)
     *
     * # SIDE CASE
     * 어노테이션이 없는 경우 - MissingAnnotationException
     */
    JavaFactoryApiParsed  parseApiReferences(PsiClass apiInterface);

    /**
     *
     *  # JOB
     * 해당 클래스가 @JavaFactoryApi인 경우, API 관련 참조 정보만 추출
     * (기본 구현체, fixture, referencedApi, 등등)
     *
     * # SIDE CASE
     * 어노테이션이 없는 경우 - MissingAnnotationException
     */
   JavaFactoryPatternParsed parsePattern(PsiClass clazz);

}
