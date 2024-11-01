package org.example.repositories.custom;

import org.example.dtos.reponse.CdeResponse;
import org.example.dtos.request.CdeRequest;
import org.springframework.data.domain.Page;

public interface CdeRepoCustom {
    Page<CdeResponse> searchCde(CdeRequest cdeRequest);
}
