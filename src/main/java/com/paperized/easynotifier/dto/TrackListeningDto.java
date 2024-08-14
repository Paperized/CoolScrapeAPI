package com.paperized.easynotifier.dto;

import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Data
public class TrackListeningDto {
    DQueryRequestScheduled dQueryRequestScheduled;
    String webhookUrl;
    boolean wsEnabled;
}
