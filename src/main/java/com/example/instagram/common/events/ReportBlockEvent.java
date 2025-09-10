package com.example.instagram.common.events;

public record ReportBlockEvent(
        Long feedId,
        Long userId
) {
}
