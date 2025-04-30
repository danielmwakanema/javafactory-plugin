package io.github.javafactoryplugindev.plugin.ui.pattern;

public class PatternCreationPreview {


    public static String goal(){
        return """
                 Your task is to implement a Java class based on a given domain-level interface.
                You will be provided with:
                
         
                1. A domain-level interface that defines the expected behaviors
                2. A set of referenced classes (e.g., data models, enums, utilities)
        
                Your job is to generate a complete and correct class implementation that fulfills the contract defined by the interface.
                """;
    }

    public static String rules(){
        return """
                - ✅ Implement all methods defined in the interface.
                - ✅ If method logic is unclear, use reasonable placeholders or inferred logic from referenced classes.
                - ✅ Do not modify or rename the interface or method signatures.
                - ✅ If referenced classes help clarify logic, incorporate them appropriately.
                - ✅ You may define private helper methods to improve clarity, reuse, or testability.
                - ✅ Preserve annotations on the class (JavaFactory, Lombok, Spring annotations).
                
                """;
    }

    public static String output(){
        return """
                Output must be:
                - A fully implemented `.java` class
                - Only raw Java code (no markdown, no external descriptions)
                - Do not add or remove existing annotations on the class
                """;
    }

    public static String example(){
        return """
                @Component
                @RequiredArgsConstructor
                public class DefaultAlarmReader implements AlarmReader {
        
                    private final IAlarmEntityRepository alarmEntityRepository;
                    private final Pageable defaultSizePage = Pageable.ofSize(20);
        
                    @Override
                    public List<Alarm> listAll(Long userId) {
                        return alarmEntityRepository
                                .findAlarmsUntil(userId, LocalDateTime.now(), defaultSizePage).stream()
                                .map(Alarm::fromEntity)
                                .toList();
                    }
        
                    @Override
                    public List<Alarm> listUnchecked(Long userId) {
                        return alarmEntityRepository
                                .findUncheckedAlarmsUntil(userId, LocalDateTime.now(), defaultSizePage).stream()
                                .map(Alarm::fromEntity)
                                .toList();
                    }
                }
        """;
    }
}
