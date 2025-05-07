package io.github.javafactoryplugindev.plugin.openai.storage;

import com.intellij.util.xmlb.annotations.Tag;

@Tag("OpenAiKey")
public class OpenAiKey {
    private String encoded;

    public OpenAiKey() {
    }

    public OpenAiKey(String encoded) {
        this.encoded = encoded;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }
}
