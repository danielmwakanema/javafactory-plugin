package io.github.javafactoryplugindev.plugin.ui.pattern;

public class PatternCreationPreview {


    public static String goal(){
        return """
                Your task is to implement a Java class based on a given domain-level interface.
                
                You will be provided with:
                
                1. A domain-level interface that defines the expected behaviors
                2. A set of referenced classes (e.g., data models, enums, utilities)
                """;
    }

    public static String rules(){
        return """
                - ✅ Implement all methods defined in the interface.
                - ✅ Do not modify or rename the interface or method signatures.
                - ✅ You may define private helper methods to improve clarity, reuse, or testability.
                - ✅ use only public class and defined methods in referenced classes.
                """;
    }

    public static String output(){
        return """
                 Output must be:
                 - A complete `.java` class
                 - Only pure Java code (no markdown, no external explanation)
                 - Comments allowed only **inside the Java class itself**

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
