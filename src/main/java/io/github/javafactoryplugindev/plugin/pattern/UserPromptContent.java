package io.github.javafactoryplugindev.plugin.pattern;

import java.util.ArrayList;
import java.util.List;

public class UserPromptContent {
    private List<UserPromptItem> items = new ArrayList<>();

    public UserPromptContent() {}

    public UserPromptContent(List<UserPromptItem> items) {
        this.items = items;
    }

    public List<UserPromptItem> getItems() {
        return items;
    }

    public void setItems(List<UserPromptItem> items) {
        this.items = items;
    }

    public static class UserPromptItem {
        private String key;
        private List<ReferenceFlag> flags = new ArrayList<>();

        public UserPromptItem() {
        }

        public UserPromptItem(String key, List<ReferenceFlag> flags) {
            this.key = key;
            this.flags = flags;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<ReferenceFlag> getFlags() {
            return flags;
        }

        public void setFlags(List<ReferenceFlag> flags) {
            this.flags = flags;
        }
    }

}
