package io.github.javafactoryplugindev.plugin.openai.prompt;

import java.util.List;

public class DomainApiImplTestPromptGenerator implements PromptGenerator<DomainApiImplTestPromptGenerator.GenerateDomainApiTestUserPromptRequest> {


    private static final DomainApiImplTestPromptGenerator INSTANCE = new DomainApiImplTestPromptGenerator();

    public static DomainApiImplTestPromptGenerator getInstance() {
        return INSTANCE;
    }

    private DomainApiImplTestPromptGenerator() {
    }


    @Override
    public String system() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String user(GenerateDomainApiTestUserPromptRequest request) {
        StringBuilder sb = new StringBuilder();

        // 1. 기존 테스트 클래스 (또는 템플릿)
        sb.append("### Existing Test Code\n");
        sb.append(request.targetClass()).append("\n\n");

        // 2. API 인터페이스
        sb.append("### Repository Interface\n");
        sb.append(request.api()).append("\n\n");

        // 3. 구현 클래스
        sb.append("###  Implementation\n");
        sb.append(request.implementation()).append("\n\n");

        // 4. 기타 참조 클래스들
        List<String> references = request.otherReferences();
        if (references == null || references.isEmpty()) {
            sb.append("(none)\n");
        } else {
            for (String ref : references) {
                sb.append("### Referenced Class\n");
                sb.append(ref).append("\n\n");
            }
        }

        return sb.toString();
    }

    public record GenerateDomainApiTestUserPromptRequest(
            String targetClass,      // 기존 또는 템플릿 테스트 클래스
            String api,          // 선택된 인터페이스 소스
            String implementation,   // 선택된 구현체 소스
            List<String> otherReferences  // 기타 참조 클래스 소스
    ) implements GenerateRequest {
    }


    private static final String SYSTEM_PROMPT = """
                        
                        
            # 🧪 도메인 구현체 테스트 클래스 생성 프롬프트
                        
            ## 🎯 목표
                        
            도메인 구현체 클래스에 대해 단위 테스트 클래스를 작성하시오. \s
            테스트는 인터페이스에 정의된 모든 메서드의 동작을 검증해야 하며, \s
            외부 의존성은 주어진 Fixture 클래스를 통해 대체하십시오.
                        
            ---
                        
            ## 📏 규칙
                        
            1. 인터페이스에 선언된 모든 메서드를 테스트할 것 \s
            2. 성공, 실패 케이스에 대해 수행할 것 \s
            3. 구현체 코드에 대해 의존성을 주입할 때는 fixture 객체를 우선적으로 주입할 것 \s
            4. 가능한 순수한 Java로 테스트할 것 (JUnit5 권장) \s
            5. `@DisplayName`을 활용해 어떤 테스트를 수행하는지 한글로 기록할 것 \s
                        
            ---
                        
            ## 📦 출력 형식
                        
            - 출력은 `.java` 전체 클래스 형식일 것 \s
            - 클래스에 존재하는 **모든 어노테이션은 반드시 그대로 유지할 것** \s
            - 마크다운(`\\`\\`\\`java`, 설명 등)은 포함하지 마시오 \s
            - 출력은 **Java 코드만 포함**하십시오 \s
                        
            ---
                        
            ## 💡 예시
                        
            ```java
            @Component
            @RequiredArgsConstructor
            @JavaFactoryClass(
                 javaFactoryClassType = JavaFactoryClassType.DOMAIN_API_TEST,
                 group = "alarm_alarm_reader"
            )\s
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
                @DisplayName("listAll 함수는 기준 시간 이전 알람 전체를 반환해야 한다")
                void listAll_shouldReturnAllAlarmsBeforeGivenDate() {
                    // 테스트 코드
                }
                        
                @Test
                @DisplayName("listUnchecked 함수는 체크되지 않은 항목만 반환해야 한다")
                void listUnchecked_shouldReturnOnlyUncheckedAlarms() {
                    // 테스트 코드
                }
            }
            ```
            
            """;
}
