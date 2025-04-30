package io.github.javafactoryplugindev.plugin.pattern;

import java.util.ArrayList;
import java.util.List;

public class DefaultPatternProvider {

    public static List<Pattern> getInitialPatterns() {
        List<Pattern> patterns = new ArrayList<>();

        // infra
        patterns.add(createDefaultJpaRepositoryInterfacePattern());
        patterns.add(createDefaultJpaRepositoryTestPattern());
        patterns.add(createDefaultJpaRepositoryFixturePattern());


        // domain
        patterns.add(createDefaultDomainApiImplPattern());
        patterns.add(createDefaultDomainApiTestPattern());
        patterns.add(createDefaultDomainApiFixturePattern());

        return patterns;
    }

    private static Pattern createDefaultJpaRepositoryInterfacePattern() {
        SystemPromptContent systemPrompt = new SystemPromptContent(
                """
                        ## Goal
                                                
                        Your task is to generate a JPA repository interface that extends both:
                                                
                        1. A Spring Data JPA base repository (`JpaRepository`), and \s
                        2. A custom domain-level repository interface (`IXXXRepository`).
                                                
                        The goal is to produce a fully usable Java interface suitable for production use.
                                                
                        You are provided with:
                        - A target interface definition to complete
                        - A set of related data classes (entities, enums, etc.)
                                                
                        """,
                """
                        - If the interface defines **custom methods**, you must implement them using either:
                          - **Spring Data JPA method naming rules**, or
                          - `@Query` with **JPQL/native query**.
                            
                        - ❗ Even for standard JPA methods (e.g., `findById`, `save`), you must **explicitly redeclare** the method in the target interface to **resolve ambiguity** caused by multiple inherited interfaces.  
                          This avoids **compile-time and mock test ambiguity errors**.
                            
                        - ✅ When implementing search methods that take a collection (e.g., `Set`, `List`) as a parameter:
                          - Prefer using **`IN`** queries (e.g., `WHERE x IN :collection`).
                          - If using JPQL or Native Query, always use `IN (:param)` syntax.
                          - Use method naming conventions like `findByFieldIn(Collection<?> values)` when possible.
                                                
                        """,
                """
                        Your response must contain:
                        - Output a complete `.java` interface file
                        - Do not use markdown or explanation
                        - The result must be immediately usable in a Spring project
                        """,
                """

                        ```java
                                      
                        public interface ArticleEntityRepository extends JpaRepository<ArticleEntity, Long>, IArticleEntityRepository {
                            @Override
                            ArticleEntity save(ArticleEntity entity);
                            
                            @Override
                            Optional<ArticleEntity> findById(Long id);
                            
                            @Query(
                                    value = "SELECT * FROM article_entity " +
                                            "WHERE MATCH(title, content) AGAINST (?1 IN BOOLEAN MODE) " +
                                            "ORDER BY id DESC LIMIT ?2 OFFSET ?3",
                                    nativeQuery = true
                            )
                            List<ArticleEntity> searchByKeywordWithPagination(String keyword, int limit, int offset);
                        }
                        ```
                        """
        );

        UserPromptContent userPrompt = new UserPromptContent(List.of(
                new UserPromptContent.UserPromptItem("api interface", List.of(ReferenceFlag.TARGET_API)),
                new UserPromptContent.UserPromptItem("referencedSources", List.of(ReferenceFlag.DATA, ReferenceFlag.OTHER_REFERENCED))
        ));

        return new Pattern(
                "sample_jpa_repository_pattern",
                systemPrompt,
                userPrompt,
                GenerationType.IMPLEMENTATION
        );
    }


    private static Pattern createDefaultJpaRepositoryTestPattern() {
        SystemPromptContent systemPrompt = new SystemPromptContent(
                """
                        Your task is to generate a JPA Repository test class for the given interface and its implementation.
                                                
                        You are given:
                        1. The repository interface defining the methods to test
                        2. The JPA implementation containing actual query logic
                        3. Additional user requirements for the test like data or entity class 
                        """,
                """
                        - ✅ You must use @DataJpaTest unless otherwise specified.
                        - ✅ For each method declared in the interface:
                          - Write at least one success test case, if applicable.
                          - Write at least one failure or edge case, if meaningful.
                        - ✅ Use @DisplayName with a short English description for each test.
                        - ✅ Focus on realistic, practical unit test scenarios.
                        - ✅ When accessing or modifying data (e.g., calling getters, setters, or constructors), you must only use **fields and methods explicitly declared as public**.
                        - 🚫 Do not access private members or use undefined methods or logic.
                        - 🚫 Do not set or change auto-generated Id manually
                                                
                        """,
                """
                        Your response must be a **complete `.java` class** that can be directly saved into the codebase.
                                
                        Output Rules:
                        1. Output only raw `.java` code — **do not** include markdown or explanation.
                        2. If you must assume data structure, base it strictly on the given sources.
                                                
                        """,
                """
                        import org.junit.jupiter.api.DisplayName;
                        import org.junit.jupiter.api.Test;
                        import org.springframework.beans.factory.annotation.Autowired;
                        import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
                        import java.util.HashSet;
                        import java.util.List;
                        import java.util.Set;
                                               
                        import java.util.Optional;
                                               
                        import static org.junit.jupiter.api.Assertions.*;
                                               
                        @DataJpaTest
                        class DefaultFooEntityRepositoryTest {
                                                
                            @Autowired
                            private FooEntityRepository fooEntityRepository;
                                                
                            @Test
                            @DisplayName("Find by id success case")
                            void findByIdInSuccessCase() {
                                FooEntity foo = new FooEntity(1L, "name", "content");
                                fooEntityRepository.save(foo);
                                                
                                Optional<FooEntity> fetchedFoo = fooEntityRepository.findById(1L);
                                                
                                assertTrue(fetchedFoo.isPresent());
                                assertEquals(1L, fetchedFoo.get().getId());
                            }
                          
                            @Test
                            @DisplayName("Find by id failure case")
                            void findByIdFailureCase() {
                                Optional<FooEntity> fetchedFoo = fooEntityRepository.findById(1L);
                                                
                                assertFalse(fetchedFoo.isPresent());
                            }
                                               
                            @Test
                            @DisplayName("Find by name containing success case")
                            void findByNameContainingSuccessCase() {
                                FooEntity foo = new FooEntity(1L, "Keyword1", "content",Set.of());
                                fooEntityRepository.save(foo);
                                List<FooEntity> fetchedFoo = fooEntityRepository.findByNameContaining("Keyword");
                               
                                assertFalse(fetchedFoo.isEmpty());
                                assertTrue(fetchedFoo.stream().anyMatch(it -> it.getName().contains("Keyword")));
                            }
                        }
                        """
        );

        UserPromptContent userPrompt = new UserPromptContent(List.of(
                new UserPromptContent.UserPromptItem("apiSource", List.of(ReferenceFlag.TARGET_API)),
                new UserPromptContent.UserPromptItem("implSource", List.of(ReferenceFlag.TARGET_DEFAULT_API_IMPL)),
                new UserPromptContent.UserPromptItem("referencedSources", List.of(ReferenceFlag.DATA, ReferenceFlag.OTHER_REFERENCED))
        ));

        return new Pattern(
                "sample_jpa_repository_test_pattern",
                systemPrompt,
                userPrompt,
                GenerationType.TEST
        );
    }

    private static Pattern createDefaultJpaRepositoryFixturePattern() {
        SystemPromptContent systemPrompt = new SystemPromptContent(
                """
                        Your task is to generate a Fixture class for the given JPA Repository interface.
                                
                        You are given:
                        1. A repository interface defining the methods
                        2. The corresponding JPA implementation class
                        3. data or utility classes 
                        """,
                """
                                - ✅ Simulate repository behaviors in pure java using in-memory collections (HashMap, etc).
                                - ✅ Implement Singleton Pattern:
                                  - private static instance
                                  - public static getInstance()
                                  - private constructor
                                - ✅ Use and define setField(...) to manually assign values to private fields such as `id`.
                                - ✅ Implement all interface methods, including custom ones.
                        """,
                """
                        Output must be:
                        - A complete `.java` class
                        - Only pure Java code (no markdown, no external explanation)
                        - Comments allowed only **inside the Java class itself**
                                    
                        """,
                """
                        Example:
                                
                        public class FakeAlarmEntityRepository implements IAlarmEntityRepository {
                                        
                            private static IAlarmEntityRepository INSTANCE = new FakeAlarmEntityRepository();
                                        
                            @Getter
                            private final HashMap<Long, AlarmEntity> db = new HashMap<>();
                            private final AtomicLong idGenerator = new AtomicLong();
                                        
                            private FakeAlarmEntityRepository() {}
                                        
                            public static IAlarmEntityRepository getInstance() {
                                if (INSTANCE == null) {
                                    INSTANCE = new FakeAlarmEntityRepository();
                                }
                                return INSTANCE;
                            }
                            
                            public static void setField(Object targetObject, String fieldName, Object value) {
                                            try {
                                                Field field = targetObject.getClass().getDeclaredField(fieldName);
                                                field.setAccessible(true);
                                                field.set(targetObject, value);
                                            } catch (Exception e) {
                                                throw new RuntimeException("Failed to set field " + fieldName, e);
                                            }
                                        }
                        }
                        """
        );

        UserPromptContent userPrompt = new UserPromptContent(List.of(
                new UserPromptContent.UserPromptItem("apiSource", List.of(ReferenceFlag.TARGET_API)),
                new UserPromptContent.UserPromptItem("implSource", List.of(ReferenceFlag.TARGET_DEFAULT_API_IMPL)),
                new UserPromptContent.UserPromptItem("otherReferenced", List.of(ReferenceFlag.DATA, ReferenceFlag.OTHER_REFERENCED))
        ));

        return new Pattern(
                "sample_jpa_repository_fixture_pattern",
                systemPrompt,
                userPrompt,
                GenerationType.FIXTURE
        );
    }

    private static Pattern createDefaultDomainApiImplPattern() {
        SystemPromptContent systemPrompt = new SystemPromptContent(
                """
Implement a Java class that fulfills the contract of a given domain-level interface using only the referenced classes.

You may only use public constructors, methods, and fields explicitly visible in the provided class definitions.
Do not assume the existence of utility methods, static factories (e.g., fromEntity, toDomain), mappers, or overloaded constructors.
If logic cannot be implemented using only those elements, return null or an empty list as a fallback.
    

                        """,
                """
                        - Return a valid, compilable Java class with minimal logic.
                        - implements method in api using provided classes, you can only use provided public method in user Prompt \s
                        - you can make private method like mapper method, so that can make valid implementation given referneces
                        - Use only constructors, fields, and methods explicitly visible in the provided code.
                        - Avoid any inferred helpers, mappers, or utility code.
                        
                        """,
                """
                        Return only:
                        - One complete `.java` class
                        - No markdown
                        - No external explanations or comments
                                       
                        """,
                """
                        
                        import org.example.domain.book.api.BookReader;
                             import org.example.domain.book.Book;
                             
                             import org.example.infra.bookEntity.BookEntityRepository;
                             import org.springframework.data.domain.Pageable;
                             import org.springframework.stereotype.Component;
                             
                             import java.util.List;
                             import java.util.stream.Collectors;
                             
                             @Component
                             @RequiredArgsConstructor
                             public class DefaultBookReader implements BookReader {
                             
                                 private final BookRepository bookRepository;
                             
                                 public DefaultBookReader(BookRepository bookRepository) {
                                     this.bookRepository = bookRepository;
                                 }
                             
                                 @Override
                                 public List<Book> findAll() {
                                     // TODO: implement using available repository methods
                                     return Collections.emptyList();
                                 }
                             
                                 @Override
                                 public Book findById(Long id) {
                                     // TODO: implement using available repository methods
                                     return null;
                                 }
                             }
                        """
        );

        UserPromptContent userPrompt = new UserPromptContent(List.of(
                new UserPromptContent.UserPromptItem("api", List.of(ReferenceFlag.TARGET_API)),
                new UserPromptContent.UserPromptItem("otherReferences", List.of(ReferenceFlag.DATA, ReferenceFlag.OTHER_REFERENCED, ReferenceFlag.REFERENCED_API))
        ));

        return new Pattern(
                "sample_domain_api_impl_pattern",
                systemPrompt,
                userPrompt,
                GenerationType.IMPLEMENTATION
        );
    }


    private static Pattern createDefaultDomainApiTestPattern() {
        SystemPromptContent systemPrompt = new SystemPromptContent(
                """
                        Write a unit test class for the given domain implementation class,
                        
                        You may only use public constructors, methods, and fields explicitly visible in the provided class definitions.
                        Do not assume the existence of utility methods, static factories (e.g., fromEntity, toDomain), mappers, or overloaded constructors.
                        If logic cannot be implemented using only those elements, return null or an empty list as a fallback.
                        
                        You will be provided with:
                                                                                                    
                        1. A domain-level interface that defines the expected behaviors
                        2. The implementation class of the interface
                        3. Fixtures for dependency injection (DI)
                        4. A set of referenced classes (e.g., data models, enums, utilities)
                                        
                        """,
                """
                        - Write test methods for every method declared in the interface.
                        - Each test must cover at least one success case and optionally edge/failure cases.
                        - Inject fixture objects when instantiating the implementation class.
                        - Use pure Java with JUnit 5.
                        - Annotate each test with @DisplayName using Korean to describe the test purpose.
                        - Do not invent helper methods, factories, or mock behavior beyond what is defined.
                        """,
                """
                          Return only:
                        - One complete `.java` class
                        - No markdown
                        - No external explanations or comments
                                        
                        """,
                """
                        Example:
                                
                        @Component
                        @RequiredArgsConstructor
                                
                        class DefaultAlarmReaderTest {
                                
                            private IAlarmEntityRepositoryFixture fixture;
                            private DefaultAlarmReader alarmReader;
                            private LocalDateTime now;
                                
                            @BeforeEach
                            void setup() {
                                fixture = new IAlarmEntityRepositoryFixture();
                                alarmReader = new DefaultAlarmReader(fixture);
                                now = LocalDateTime.now();
                            }
                                
                            @Test
                            @DisplayName("listAll returns all alarms before")
                            void listAll_shouldReturnAllAlarmsBeforeGivenDate() {
                                // Test code
                            }
                                
                            @Test
                            @DisplayName("listUnchecked returns all alarms before unchecked ")
                            void listUnchecked_shouldReturnOnlyUncheckedAlarms() {
                                // Test code
                            }
                        }
                        """
        );

        UserPromptContent userPrompt = new UserPromptContent(List.of(
                new UserPromptContent.UserPromptItem("api", List.of(ReferenceFlag.TARGET_API)),
                new UserPromptContent.UserPromptItem("implementation", List.of(ReferenceFlag.TARGET_DEFAULT_API_IMPL)),
                new UserPromptContent.UserPromptItem("otherReferences", List.of(ReferenceFlag.DATA, ReferenceFlag.OTHER_REFERENCED))
        ));

        return new Pattern(
                "sample_domain_api_test_pattern",
                systemPrompt,
                userPrompt,
                GenerationType.TEST
        );
    }

    private static Pattern createDefaultDomainApiFixturePattern() {
        SystemPromptContent systemPrompt = new SystemPromptContent(
                """
                        Write a test fixture class for a given domain-level API interface.
                        This fixture must wrap the real implementation class (`DefaultXxx`) and simulate its behavior for testing environments.

                        You are given:
                        - A domain-level API interface
                        - The corresponding real implementation class (`DefaultXxx`)
                        - Related dependency fixtures (`FakeRepository.getInstance()` etc.)
                                        """,
                """
                        - ✅ Class name must be `FakeDefault[InterfaceName]`.
                        - ✅ Singleton pattern must be implemented:
                          - private static instance field
                          - public static getInstance() method
                          - private constructor
                        - ✅ The fixture must internally instantiate the corresponding `DefaultXxx` implementation.
                          - Use `FakeRepository.getInstance()` or other fixtures for dependencies.
                        - ✅ All methods must directly delegate to `getInstance().method(...)`.
                        - ✅ Preserve the `@JavaFactoryClass` annotation exactly as given (do not modify, add, or remove annotations).
                                        """,
                """
                        Output must be:
                        - A complete `.java` class file
                        - Only pure Java code without markdown blocks, external explanations, or additional comments
                        - Inline Java comments allowed inside the class if needed.
                        """,
                """
                        Example:
                                
                                
                        public class FakeDdReader implements DdReader {
                                
                            private static DdReader INSTANCE;
                                
                            private FakeDdReader() {}
                                
                            public static DdReader getInstance() {
                                if (INSTANCE == null) {
                                    INSTANCE = new DefaultDdReader(FakeIE1Repository.getInstance());
                                }
                                return INSTANCE;
                            }
                                
                            @Override
                            public Dd findById(Long id) {
                                return getInstance().findById(id);
                            }
                        }
                        """
        );

        UserPromptContent userPrompt = new UserPromptContent(List.of(
                new UserPromptContent.UserPromptItem("apiSource", List.of(ReferenceFlag.TARGET_API)),
                new UserPromptContent.UserPromptItem("implSource", List.of(ReferenceFlag.TARGET_DEFAULT_API_IMPL)),
                new UserPromptContent.UserPromptItem("otherReferenced", List.of(ReferenceFlag.DATA, ReferenceFlag.OTHER_REFERENCED))
        ));

        return new Pattern(
                "sample_domain_api_fixture_pattern",
                systemPrompt,
                userPrompt,
                GenerationType.FIXTURE
        );
    }
}