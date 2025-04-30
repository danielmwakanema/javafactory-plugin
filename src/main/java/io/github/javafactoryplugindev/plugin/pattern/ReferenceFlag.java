package io.github.javafactoryplugindev.plugin.pattern;

public enum ReferenceFlag {
    // 🎯 타겟 API 관련
    TARGET_API,
    TARGET_DEFAULT_API_IMPL,
    TARGET_DEFAULT_API_FIXTURE,

    // 🔗 보조 API 관련
    REFERENCED_API,
    REFERENCED_API_IMPL,
    REFERENCED_API_FIXTURE,

    // 🧱 데이터 모델
    DATA,

    OTHER_REFERENCED
}
