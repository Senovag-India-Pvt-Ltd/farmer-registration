package com.sericulture.registration.model.dto.village;

import lombok.Data;

@Data
public class VillageDTO {
    private Long villageId;
    private String villageName;
    private Long stateId;
    private Long districtId;
    private Long talukId;
    private Long hobliId;
    private String stateName;
    private String districtName;
    private String talukName;
    private String hobliName;

    public VillageDTO(){

    }

    public VillageDTO(Long villageId,String villageName, Long stateId, Long districtId, Long talukId,Long hobliId, String stateName, String districtName, String talukName, String hobliName) {
        this.villageId = villageId;
        this.villageName = villageName;
        this.stateId = stateId;
        this.districtId = districtId;
        this.talukId = talukId;
        this.hobliId = hobliId;
        this.stateName = stateName;
        this.districtName = districtName;
        this.talukName = talukName;
        this.hobliName = hobliName;
    }
}
