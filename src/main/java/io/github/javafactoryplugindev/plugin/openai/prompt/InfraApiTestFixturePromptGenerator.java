package io.github.javafactoryplugindev.plugin.openai.prompt;

import java.util.List;

public class InfraApiTestFixturePromptGenerator implements PromptGenerator<InfraApiTestFixturePromptGenerator .GenerateRepositoryTestFixtureUserPromptRequest> {


    private static final  InfraApiTestFixturePromptGenerator  INSTANCE = new  InfraApiTestFixturePromptGenerator ();
    public static  InfraApiTestFixturePromptGenerator  getInstance() {
        return INSTANCE;
    }
    private  InfraApiTestFixturePromptGenerator  (){}


    @Override
    public String system() {
        return SYSTEM_PROMPT;
    }


    @Override
    public String user(GenerateRepositoryTestFixtureUserPromptRequest request) {
        StringBuilder sb = new StringBuilder();

        // 1. 타겟 클래스
        sb.append("### Target Class\n");
        sb.append(request.targetTestFixtureSource()).append("\n\n");

        // 2. API 인터페이스
        sb.append("### Repository Interface\n");
        sb.append(request.apiSource()).append("\n\n");

        // 3. JPA 구현체
        sb.append("### JPA Implementation\n");
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

    public record GenerateRepositoryTestFixtureUserPromptRequest(
            String targetTestFixtureSource, // ✅ Target Class
            String apiSource,               // ✅ Repository Interface
            String implSource,              // ✅ JPA Implementation
            List<String> otherReferenced    // ✅ Referenced Class (복수)
    ) implements GenerateRequest {
    }


    private static final String SYSTEM_PROMPT = """
            # 🧪 싱글턴 기반 JPA Repository 테스트 픽스처 클래스 생성 프롬프트
             
             ---
             
             ## 🎯 목표
             
             JPA Repository 인터페이스, 해당 구현 클래스, 관련 엔티티 클래스를 기반으로 \s
             **싱글턴 기반 테스트용 픽스처 클래스**를 작성하시오.
             
             ---
             
             ## 📌 작업 설명
             
             실제 데이터베이스에 의존하지 않고 **단위 테스트를 수행할 수 있도록**, \s
             **메모리 기반의 로직**으로 Repository 메서드를 오버라이드하여 구현합니다.
             
             이 픽스처는 여러 테스트 클래스에서 공유될 수 있도록 **싱글턴 패턴**으로 구현되어야 합니다. \s
             모든 인터페이스 메서드는 빠짐없이 구현해야 하며, JPA 기본 메서드 외에 커스텀 메서드도 포함됩니다.
             
             - ID 생성은 `AtomicLong`을 활용합니다. \s
             - 데이터 저장은 `HashMap<Long, Entity>`를 사용합니다. \s
             - ID가 없는 경우에는 `FakeSetter.setField(...)`를 통해 수동으로 ID를 설정합니다.
             
             ---
             
             ## 📏 출력 규칙
             
             - 출력은 `.java` 파일 전체여야 하며, **순수 Java 코드만 포함**해야 합니다.
             - **마크다운 문법(예: ```java), 외부 설명, 주석 등은 출력에 포함하지 마세요.**
             - 추가 로직이 필요한 경우, **클래스 내부 주석으로만 설명하세요.**
             - 클래스에 이미 존재하는 **어노테이션은 절대 제거하거나 수정하지 마세요.**
             
             ---
             
             ## ✅ 추가 구현 조건
             
             - 클래스 이름은 반드시 `Fake[인터페이스 이름]` 형식으로 작성하세요.
             - 반드시 **싱글턴 패턴**을 따르도록 구현하세요:
               - `private static INSTANCE` 필드 정의
               - `public static getInstance()` 메서드 제공
               - 생성자는 `private`으로 선언
             - `db`, `idGenerator` 등 내부 상태는 **정적(static)이 아닌 인스턴스 변수**로 선언할 것
             
             ---
             
             ## 💡 출력 예시
             
             ```java
             @JavaFactoryClass(
                     javaFactoryClassType = JavaFactoryClassType.INFRA_REPOSITORY_FIXTURE,
                     references = {AlarmEntityRepository.class, AlarmEntity.class, IAlarmEntityRepository.class}
             )
             public class FakeAlarmEntityRepository implements IAlarmEntityRepository {
             
                 private static IAlarmEntityRepository INSTANCE = new FakeAlarmEntityRepository();
             
                 @Getter
                 private final HashMap<Long, AlarmEntity> db = new HashMap<>();
                 private final AtomicLong idGenerator = new AtomicLong();
             
                 private FakeAlarmEntityRepository() {
                 }
             
                 public static IAlarmEntityRepository getInstance() {
                     if (INSTANCE == null) {
                         INSTANCE = new FakeAlarmEntityRepository();
                     }
                     return INSTANCE;
                 }
             
                 public AlarmEntity save(AlarmEntity alarmEntity) {
                     if (alarmEntity != null && alarmEntity.getId() != null) {
                         db.put(alarmEntity.getId(), alarmEntity);
                         return alarmEntity;
                     }
                     var newId = idGenerator.incrementAndGet();
                     FakeSetter.setField(alarmEntity, "id", newId);
             
                     db.put(newId, alarmEntity);
                     return alarmEntity;
                 }
             
                 @Override
                 public int updateCheckedByUserId(Boolean checked, Long userId) {
                     int cnt = 0;
                     for (AlarmEntity entity : db.values()) {
                         if (entity.getUserId().equals(userId)) {
                             entity.setChecked(checked);
                             cnt++;
                         }
                     }
                     return cnt;
                 }
             
                 @Override
                 public int updateCheckedById(Boolean checked, Long userId, Long alarmId) {
                     AlarmEntity entity = db.get(alarmId);
                     if (entity != null && entity.getUserId().equals(userId)) {
                         entity.setChecked(checked);
                         return 1;
                     }
                     return 0;
                 }
             }
            """;
}
