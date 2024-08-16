package com.paperized.easynotifier.service;

import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.generated.easynotifier.model.FindCandidatesRequest;
import com.paperized.generated.easynotifier.model.LinkedinCandidateDto;
import com.paperized.generated.easynotifier.model.LinkedinCandidatesTracked;

import java.util.List;

public interface LinkedinService {
    List<LinkedinCandidateDto> findCandidates(FindCandidatesRequest findCandidatesRequest);

    LinkedinCandidatesTracked findCandidatesTracked(FindCandidatesRequest findCandidatesRequest, TrackListeningDto trackListeningDto);
}
