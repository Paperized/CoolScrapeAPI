package com.paperized.easynotifier.model.webhookfilter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DOnChangeSaveMode {
    SAVE_ALL("save-all"),
    SAVE_LAST("save-last");

    private final String value;
}
