package io.github.javafactoryplugindev.plugin.openai.storage;

import java.util.Base64;

public class KeyEncoder {


    public static String encode(String key) {
        if (key == null || key.isBlank()) return "";
        return Base64.getEncoder().encodeToString(key.getBytes());
    }

    public static String decode(String encoded) {
        if (encoded == null || encoded.isBlank()) return "";
        return new String(Base64.getDecoder().decode(encoded));
    }
}
