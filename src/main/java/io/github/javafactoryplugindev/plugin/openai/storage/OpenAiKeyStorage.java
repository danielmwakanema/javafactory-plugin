package io.github.javafactoryplugindev.plugin.openai.storage;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "OpenAiKeyStorage",
        storages = @Storage("openai_key_storage.xml") // 저장 위치 (자동 관리)
)
public class OpenAiKeyStorage implements PersistentStateComponent<OpenAiKey> {

    private OpenAiKey state = new OpenAiKey();

    @Override
    public @Nullable OpenAiKey getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull OpenAiKey state) {
        this.state = state;
    }

    public void saveKey(String rawKey) {
        KeyEncoder encoder = new KeyEncoder();
        state.setEncoded(encoder.encode(rawKey));
    }

    public String getDecodedKey() {
        KeyEncoder encoder = new KeyEncoder();
        return encoder.decode(state.getEncoded());
    }

    // IntelliJ용 싱글톤 인스턴스
    public static OpenAiKeyStorage getInstance(Project project) {
        return project.getService(OpenAiKeyStorage.class);
    }
}
