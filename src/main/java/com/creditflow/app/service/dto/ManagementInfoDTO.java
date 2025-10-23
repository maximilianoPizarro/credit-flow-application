package com.creditflow.app.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.json.bind.annotation.JsonbProperty;
import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModel;
/**
 *  DTO to emulate /management/info response
 */
@RegisterForReflection
public class ManagementInfoDTO {

    public List<String> activeProfiles = new ArrayList<>();

    @JsonbProperty("display-ribbon-on-profiles")
    public String displayRibbonOnProfiles;
}
