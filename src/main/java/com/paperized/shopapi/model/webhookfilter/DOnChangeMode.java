package com.paperized.shopapi.model.webhookfilter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DOnChangeMode {
    SEND_ONLY_DIFF_ELEMENTS("send-only-diff-elements"),
    SEND_ALL_ONLY_IF_DIFF("send-all-only-if-diff");

    private final String value;
}
