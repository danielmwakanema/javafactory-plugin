package io.github.javafactoryplugindev.plugin.openai.prompt;

import java.util.List;

public class DomainApiImplPromptGenerator implements PromptGenerator<DomainApiImplPromptGenerator.GenerateDomainApiImplUserPromptRequest> {


    private static final DomainApiImplPromptGenerator INSTANCE = new DomainApiImplPromptGenerator();
    public static DomainApiImplPromptGenerator getInstance() {
        return INSTANCE;
    }
    private DomainApiImplPromptGenerator(){}


    @Override
    public String system() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String user(GenerateDomainApiImplUserPromptRequest request) {
        StringBuilder sb = new StringBuilder();

        // 1. 타겟 클래스 (구현 대상 클래스)
        sb.append("### Target Class\n");
        sb.append(request.targetClass()).append("\n\n");

        // 2. API 인터페이스
        sb.append("### Interface\n");
        sb.append(request.api()).append("\n\n");

        // 3. 기타 참조 클래스들
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

    public record GenerateDomainApiImplSystemPromptRequest(
    ) implements GenerateRequest {
    }
    public record GenerateDomainApiImplUserPromptRequest(
            String targetClass,          // 실제로 구현할 클래스의 스켈레톤
            String api,    // 유저가 선택한 인터페이스 본문
            List<String> otherReferences// 나머지 참조 클래스들의 본문

    ) implements GenerateRequest {
    }

    private static final String SYSTEM_PROMPT = """
            ## Goal
                        
            Your task is to implement a Java class based on a given domain-level interface.
            You will be provided with:
                        
            1. A target implementation class skeleton (may be empty or partially filled)
            2. A domain-level interface that defines the expected behaviors
            3. A set of referenced classes (e.g., data models, enums, utilities)
                        
            Your job is to generate a complete and correct class implementation that fulfills the contract defined by the interface.
                        
            ---
                        
            ## Key Instructions
                       
            - ✅ Implement all methods defined in the interface 
            - ✅ If the method logic is unclear, use reasonable placeholders or inferred logic from referenced classes.
            - ✅ Do not modify or rename the interface or method signatures.
            - ✅ If referenced classes help clarify the logic, consider incorporating them appropriately.
            - ✅ You may define private helper methods within the class to split complex logic if it helps improve clarity, reuse, or testability.
            - ✅ preserve annotations on class like  JavaFactory or lombok,spring annotations
            
            ---
                        
            ## Output Format
                        
            Your response must be a **fully implemented `.java` class** that can be used directly in the codebase.
                        
            ---
                        
            ## Output Rules
                        
            - Only the final `.java` interface definition
            - No extra markdown or description
            - Do **not** add or remove annotations on the class
                
            ---
            ### Example interface as input\s
                        
            [Interface]
            
            @JavaFactoryClass(
                 javaFactoryClassType = JavaFactoryClassType.DOMAIN_API,
                 group = "alarm_alarm_reader",
                 referencedApi = { AlarmReader.class},
                 referencedData = {Alarm.class, AlarmEntity.class }
            )
            public interface AlarmReader {
                List<Alarm> listAll(Long userId);
                List<Alarm> listUnchecked(Long userId);
                List<Alarm> listAll(Long userId, LocalDateTime since);
                List<Alarm> listUnchecked(Long userId, LocalDateTime since );
            }
                        
            ## Expected Output
            
            ```java
            
            @Component
            @RequiredArgsConstructor
            \s
            @JavaFactoryClass(
                 javaFactoryClassType = JavaFactoryClassType.DOMAIN_API_IMPL,
                 group = "alarm_alarm_reader"
            )
            public class DefaultAlarmReader implements AlarmReader {
                        
                private final IAlarmEntityRepository alarmEntityRepository;
               \s
                private final Pageable defaultSizePage = Pageable.ofSize(20);
               \s
                @Override
                public List<Alarm> listAll(Long userId) {
                    return alarmEntityRepository
                            .findAlarmsUntil(userId, LocalDateTime.now(),defaultSizePage).stream()
                            .map(Alarm::fromEntity).toList();
                }
                        
                @Override
                public List<Alarm> listUnchecked(Long userId) {
                    return alarmEntityRepository
                            .findUncheckedAlarmsUntil(userId, LocalDateTime.now(),defaultSizePage).stream()
                            .map(Alarm::fromEntity).toList();
                }
                        
                @Override
                public List<Alarm> listAll(Long userId, LocalDateTime since) {
                    return alarmEntityRepository
                            .findAlarmsUntil(userId, since,defaultSizePage).stream()
                            .map(Alarm::fromEntity).toList();
                }
                        
                @Override
                public List<Alarm> listUnchecked(Long userId, LocalDateTime since) {
                    return alarmEntityRepository
                            .findUncheckedAlarmsUntil(userId, since,defaultSizePage).stream()
                            .map(Alarm::fromEntity).toList();
                }
            }
            
            ```
            """;
}
