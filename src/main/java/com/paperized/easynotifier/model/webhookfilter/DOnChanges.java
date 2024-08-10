package com.paperized.easynotifier.model.webhookfilter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Jacksonized
@Builder
@Getter
@Setter
public class DOnChanges implements Serializable {
    private DOnChangeMode mode;
    private DOnChangeSaveMode saveMode;
    private List<String> propertiesToCheck;
}
