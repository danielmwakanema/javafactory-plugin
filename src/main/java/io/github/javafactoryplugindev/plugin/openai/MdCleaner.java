package io.github.javafactoryplugindev.plugin.openai;

import java.util.ArrayList;
import java.util.List;

public class MdCleaner {
    /**
     * 마크다운 코드 블럭 중 가장 앞의 시작(``` 또는 ```java)과
     * 가장 마지막의 종료(```)만 제거한다.
     */
    public static String cleanJavaCode(String content) {
        if(content == null || content.isBlank())
            return "";

        String trimmed = content.trim();

        if (trimmed.startsWith("package ")) {
            return content; // 순수 자바 코드로 판단
        }

        List<String> lines = content.lines().toList();
        int startIdx = -1;
        int endIdx = -1;

        // 1. 시작 블럭 찾기 (``` 또는 ```java)
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.equals("```java") || line.equals("```")) {
                startIdx = i;
                break;
            }
        }

        // 2. 종료 블럭 찾기 (```), 끝에서부터 탐색
        for (int i = lines.size() - 1; i > startIdx; i--) {
            String line = lines.get(i).strip();
            if (line.equals("```")) {
                endIdx = i;
                break;
            }
        }

        // 조건에 맞지 않으면 원본 그대로 반환
        if (startIdx == -1 || endIdx == -1 || startIdx >= endIdx) {
            return content;
        }

        // 3. 시작, 종료 블럭 제외한 나머지 라인 반환
        List<String> cleaned = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (i != startIdx && i != endIdx) {
                cleaned.add(lines.get(i));
            }
        }

        return String.join("\n", cleaned);
    }
}
