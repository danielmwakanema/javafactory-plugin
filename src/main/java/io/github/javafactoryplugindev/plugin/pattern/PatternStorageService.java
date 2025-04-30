package io.github.javafactoryplugindev.plugin.pattern;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "JavaFactoryPatternStorage",
        storages = @Storage("javaFactoryPatterns.xml")
)
@Service(Service.Level.PROJECT)
public final class PatternStorageService implements PersistentStateComponent<PatternStorageService.State> {

    private State state = new State();

    public static PatternStorageService getInstance(Project project) {
        PatternStorageService storage = project.getService(PatternStorageService.class);
        initializeStorage(storage);
        return storage;
    }

    private static void initializeStorage(PatternStorageService storage) {
        List<Pattern> patterns = storage.getAllPatterns();
        if (patterns == null) {
            patterns = new ArrayList<>();
        }

        var initialPatterns = DefaultPatternProvider.getInitialPatterns();

        if (patterns.isEmpty()) {
            for (var pattern : initialPatterns) {
                storage.savePattern(pattern);
            }
        }
    }

    @Override
    public @NotNull State getState() {

        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;

    }


    public void savePattern(@NotNull Pattern pattern) {
        state.patterns.removeIf(p -> p.getName().equals(pattern.getName()));
        state.patterns.add(pattern);
    }

    public List<Pattern> getAllPatterns() {
        return state.patterns;
    }

    public Pattern findByName(String name) {
        return state.patterns.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .map(p -> {
                    if (p.getGenerationType() == null) {
                        p.setGenerationType(GenerationType.NONE);
                    }
                    return p;
                })
                .orElse(null);
    }

    public void deletePattern(String name) {
        state.patterns.removeIf(p -> p.getName().equals(name));
    }

    public static class State {
        public List<Pattern> patterns = new ArrayList<>();
    }
}
