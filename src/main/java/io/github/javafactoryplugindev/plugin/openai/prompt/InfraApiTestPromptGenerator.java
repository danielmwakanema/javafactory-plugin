package io.github.javafactoryplugindev.plugin.openai.prompt;

import java.util.List;

public class InfraApiTestPromptGenerator implements PromptGenerator<InfraApiTestPromptGenerator.GenerateInfraTestUserPromptRequest> {

    private static final InfraApiTestPromptGenerator INSTANCE = new InfraApiTestPromptGenerator();
    public static InfraApiTestPromptGenerator getInstance() {
        return INSTANCE;
    }
    private InfraApiTestPromptGenerator(){}


    @Override
    public String system() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String user(GenerateInfraTestUserPromptRequest request) {
        StringBuilder sb = new StringBuilder();

        // 1. 테스트 대상 클래스
        sb.append("### Target Test Class\n");
        sb.append(request.testSource()).append("\n\n");

        // 2. 인터페이스
        sb.append("### Repository Interface\n");
        sb.append(request.apiSource()).append("\n\n");

        // 3. 구현체
        sb.append("### JPA Implementation\n");
        sb.append(request.implSource()).append("\n\n");

        // 4. 기타 참조 클래스들
        List<String> refs = request.referencedSources();
        if (refs == null || refs.isEmpty()) {
            sb.append("(none)\n\n");
        } else {
            for (String ref : refs) {
                sb.append("### Referenced Class\n");
                sb.append(ref).append("\n\n");
            }
        }



        return sb.toString();
    }



    public record GenerateInfraTestUserPromptRequest(
            String testSource,
            String apiSource,
            String implSource,
            List<String> referencedSources // = otherReferences
    ) implements GenerateRequest {
    }


    private static final String SYSTEM_PROMPT = """
            ## Goal
            Your task is to generate a JPA Repository test class for the given interface and its implementation.

            You are given:
            1. A target test class (existing or to be modified)
            2. The repository interface defining the methods to test
            3. The JPA implementation containing actual query logic
            4. Additional user requirements for the test

            ---

            ## Key Instructions

            - ✅ You must use `@DataJpaTest` unless otherwise specified.
            - ✅ **Preserve all existing annotations** in the original test class or any related classes.
            - ✅ For **each method in the interface**, you must:
              - Write at least one **success test case** if possible
              - Write at least one **failure or edge case** test case if possible
            - ✅ Focus on realistic unit test scenarios — aim for simple, readable tests.
            - ✅ add DisplayName in each test scenarios
            - ✅ Preserve All annotaions in class.
            

            ---

            ## Output Format

            Your response must be a **complete `.java` class** that can be directly saved into the codebase.

            ---

            ## Output Rules

            1. Output **only raw Java code** — do **not** include any markdown like ```java.
            2. Do **not** include explanations or comments outside of the code.
            3. If additional context is required, include it as **Java comments at the bottom of the file**.
            4. Do **not** add or remove annotations on the class
            ---

            ## Output example

            ```java
            @DataJpaTest
            @JavaFactoryClass(
                    javaFactoryClassType = JavaFactoryClassType.INFRA_REPOSITORY_TEST,
                    references = {AlarmEntityRepository.class, AlarmEntity.class, IAlarmEntityRepository.class}
            )
            public class FakeIAlarmEntityRepositoryTest {

                private FakeIAlarmEntityRepository repository;

                @BeforeEach
                void setUp() {
                    repository = new FakeIAlarmEntityRepository();
                }

                @Test
                void save_shouldAssignIdWhenIdIsNull() {
                    AlarmEntity entity = new AlarmEntity();
                    entity.setChecked(false);
                    entity.setUserId(1L);

                    AlarmEntity saved = repository.save(entity);

                    assertNotNull(saved.getId());
                    assertEquals(1L, saved.getUserId());
                }

                @Test
                void save_shouldOverrideWhenIdExists() {
                    AlarmEntity entity = new AlarmEntity();
                    entity.setId(10L);
                    entity.setUserId(1L);
                    repository.save(entity);

                    AlarmEntity updated = new AlarmEntity();
                    updated.setId(10L);
                    updated.setUserId(2L);

                    repository.save(updated);
                    assertEquals(2L, repository.db.get(10L).getUserId());
                }

                // 🔥 Failure case added
                @Test
                void save_shouldNotSaveNullEntity() {
                    assertThrows(NullPointerException.class, () -> repository.save(null));
                }
            }
            """;
}
