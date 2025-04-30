package io.github.javafactoryplugindev.plugin.openai.prompt;

import java.util.List;

public class DomainApiFixturePromptGenerator implements PromptGenerator<DomainApiFixturePromptGenerator.GenerateDomainTestFixtureUserPromptRequest> {

    private static DomainApiFixturePromptGenerator INSTANCE;

    private DomainApiFixturePromptGenerator() {
    }

    public static DomainApiFixturePromptGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DomainApiFixturePromptGenerator();
        }
        return INSTANCE;
    }

    @Override
    public String system() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String user(GenerateDomainTestFixtureUserPromptRequest request) {
        StringBuilder sb = new StringBuilder();

        // 1. 타겟 클래스
        sb.append("### Target Class\n");
        sb.append(request.targetTestFixtureSource()).append("\n\n");

        // 2. API 인터페이스
        sb.append("### API Interface\n");
        sb.append(request.apiSource()).append("\n\n");

        // 3. JPA 구현체
        sb.append("### API Implementation\n");
        sb.append(request.implSource()).append("\n\n");

        // 4. 기타 참조 클래스들
        List<String> others = request.otherReferenced();
        if (others == null || others.isEmpty()) {
            sb.append("(none)\n");
        } else {
            for (String ref : others) {
                sb.append("### Referenced Class\n");
                sb.append(ref).append("\n\n");
            }
        }

        return sb.toString();
    }


    public record GenerateDomainTestFixtureUserPromptRequest(
            String targetTestFixtureSource, // ✅ Target Class
            String apiSource,               // ✅ Repository Interface
            String implSource,              // ✅ JPA Implementation
            List<String> otherReferenced    // ✅ Referenced Class (복수)
    ) implements GenerateRequest {
    }

    private static final String SYSTEM_PROMPT = """
                   # 🧪 도메인 API용 테스트 픽스처 클래스 생성 프롬프트
                   
                   ---
                   
                   ## 🎯 목표
                   
                   도메인 인터페이스에 대응하는 **테스트용 Fixture 클래스**를 작성하시오. \s
                   이 클래스는 실제 구현체(`DefaultXxx`)에 의존성을 주입하여 래핑하며, \s
                   테스트 환경에서 도메인 구현체를 대체하거나 주입 가능한 형태로 구성되어야 합니다.
                   
                   ---
                   
                   ## 📌 구현 규칙
                   
                   1. 클래스 이름은 `Fake[인터페이스 이름]` 형식으로 생성할 것 \s
                   2. **싱글턴 패턴**으로 구성할 것 (`private static INSTANCE`, `getInstance()` 메서드 포함) \s
                   3. 내부 구현체는 `DefaultXxx`를 사용하고, 필요한 의존성은 `FakeRepository.getInstance()` 등으로 주입할 것 \s
                   4. 인터페이스의 모든 메서드는 `getInstance().메서드()` 형식으로 위임 구현할 것 \s
                   5. 클래스에 포함된 `@ JavaFactory` 어노테이션은 **수정 없이 그대로 유지**할 것 \s
                   
                   ---
                   
                   ## 📦 출력 형식
                   
                   - `.java` 전체 파일 형식
                   - **순수 Java 코드만 출력**할 것
                   - 마크다운(` ``` `), 외부 설명, 주석 등은 포함하지 마시오
                   
                   ---
                   
                   ## 💡 출력 예시
                   
                   ```java
                   @JavaFactoryClass(
                           javaFactoryClassType = JavaFactoryClassType.DOMAIN_API_FIXTURE,
                           group = "domain_dg_dd_reader",
                           // 제작에 필요한 함수를 추가하세요
                           referencedApi= {},
                           referencedData ={}
                   )
                   public class FakeDdReader implements DdReader {
                   
                       private static DdReader INSTANCE;
                   
                       private FakeDdReader() {}
                   
                       public static DdReader getInstance(){
                           if(INSTANCE == null){
                               INSTANCE = new DefaultDdReader(FakeIE1Repository.getInstance());
                           }
                           return INSTANCE;
                       }
                   
                       @Override
                       public Dd findById(Long id) {
                           return getInstance().findById(id);
                       }
                   }     
            """;
}
