package io.github.javafactoryplugindev.plugin.openai.prompt;

import java.util.List;

public class InfraApiImplPromptGenerator implements PromptGenerator< InfraApiImplPromptGenerator.GenerateApiImplUserRequest> {


    private static final InfraApiImplPromptGenerator INSTANCE = new InfraApiImplPromptGenerator();
    public static InfraApiImplPromptGenerator getInstance() {
        return INSTANCE;
    }
    private InfraApiImplPromptGenerator(){}


    @Override
    public String system() {
        return JPA_IMPL_SYSTEM_PROMPT;
    }

    @Override
    public String user(GenerateApiImplUserRequest request) {
        StringBuilder builder = new StringBuilder();

        // 1. Target Class
        builder.append("### Target Class\n\n");
        builder.append(request.targetSource()).append("\n\n");

        // 2. Interface
        builder.append("### Interface\n\n");
        builder.append(request.apiSource()).append("\n\n");

        // 3. Referenced Classes
        List<String> referencedSources = request.referencedSources();
        if (referencedSources == null || referencedSources.isEmpty()) {
            builder.append("(none)\n");
        } else {
            for (String ref : referencedSources) {
                builder.append("### Referenced Class\n");
                builder.append(ref).append("\n\n");
            }
        }

        return builder.toString();
    }



    public static record GenerateApiImplUserRequest(
            String targetSource,
            String apiSource,
            List<String> referencedSources
    ) implements GenerateRequest {
    }

    private static final String JPA_IMPL_SYSTEM_PROMPT = """
            ## Goal

            Your task is to generate a new JPA repository interface that extends both the given Spring Data JPA base repository (`JpaRepository`) and a domain-level custom repository interface (`IXXXRepository`).
                        
            You are given:
            1. A target repository interface skeleton
            2. A custom repository interface to be implemented
            3. A set of referenced classes (domain models, enums, etc.)

            ## Key Rules
            - If the interface defines **custom methods**, you must implement them using either:
              - **Spring Data JPA method naming rules**, or
              - `@Query` with **JPQL/native query**.

            - ❗ Even for standard JPA methods (e.g., `findById`, `save`), you must **explicitly redeclare** the method in the target interface to **resolve ambiguity** caused by multiple inherited interfaces.  
              This avoids **compile-time and mock test ambiguity errors**.
              
            - ✅ **Preserve all existing annotations** in the original interface or class unless explicitly instructed otherwise.  

            ## Output Format

            Your response must contain:
            - Only the final `.java` interface definition
            - No extra markdown or description
            - Do **not** add or remove annotations on the class

            ## Example

            ### Interface - input interface example

            ```java
            /**
             * 도메인 계층에서 사용하는 추상 레포지토리입니다.
             */
             
              @JavaFactoryClass(
                 javaFactoryClassType =  JavaFactoryClassType.INFRA_REPOSITORY,
                 referencedApi = { IArticleEntityRepository.class },
                 referencedData = {ArticleEntity.class}
                 group = "article_article"
             )
            public interface IArticleEntityRepository {
                // TODO: define methods if needed

                ArticleEntity save(ArticleEntity entity);

                Optional<ArticleEntity> findById(Long id);

                /**
                 * 특정 키워드가 포함된 게시글을 최신순으로 조회합니다 (native query 사용).
                 */
                List<ArticleEntity> searchByKeywordWithPagination(String keyword, int limit, int offset);

            }
            ```

            ### target class  - expected output example

            ```java
    
            @JavaFactoryClass(
                 javaFactoryClassType =  JavaFactoryClassType.INFRA_REPOSITORY_IMPL,
                 group = "article_article"
            )
            public interface ArticleEntityRepository extends JpaRepository<ArticleEntity, Long>, IArticleEntityRepository {
                @Override
                ArticleEntity save(ArticleEntity entity);

                @Override
                Optional<ArticleEntity> findById(Long id);

                /**
                 * 특정 키워드가 포함된 게시글을 최신순으로 조회합니다 (native query 사용).
                 */
                @Query(
                        value = "SELECT * FROM article_entity " +
                                "WHERE MATCH(title, content) AGAINST (?1 IN BOOLEAN MODE) " +
                                "ORDER BY id DESC LIMIT ?2 OFFSET ?3",
                        nativeQuery = true
                )
                List<ArticleEntity> searchByKeywordWithPagination(String keyword, int limit, int offset);
            }
            ```
            """;
}
