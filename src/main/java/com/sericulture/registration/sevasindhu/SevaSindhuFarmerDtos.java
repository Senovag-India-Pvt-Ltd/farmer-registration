package com.sericulture.registration.sevasindhu;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Request/response models for the Seva Sindhu citizen farmer check and self-registration. */
public final class SevaSindhuFarmerDtos {

    private SevaSindhuFarmerDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckRequest {
        private String fruitsId;
    }

    /**
     * status:
     *   REGISTERED       - farmer exists (now linked to the request); continue to the application
     *   NOT_REGISTERED   - not in e-Reshme; FRUITS data returned for self-registration
     *   MOBILE_MISMATCH  - registered, but Seva Sindhu's mobile differs from the registered one
     *   NOT_IN_FRUITS    - FRUITS has no farmer with this id
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CheckResponse {
        private String status;
        private String message;
        private String fruitsId;
        private String farmerName;
        private String farmerNameInKannada;
        private String fatherName;
        /** Masked registered mobile, e.g. 98XXXXXX45. */
        private String registeredMobile;
        /** Seva Sindhu mobile (masked) when it was supplied; the citizen cannot change it. */
        private String sevaSindhuMobile;
        private Boolean mobileFromSevaSindhu;
        private String addressText;
        private String pincode;
        private Long suggestedDistrictId;
        private Long suggestedTalukId;
        private Long suggestedHobliId;
        private Long suggestedVillageId;
        private List<LandItem> lands;
        /** Land records whose location could not be matched to e-Reshme masters (registration still allowed). */
        private List<String> warnings;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LandItem {
        private String surveyNumber;
        private String hissa;
        private Long acre;
        private Long gunta;
        private Double fGunta;
        private String ownerName;
        private String districtName;
        private String talukName;
        private String hobliName;
        private String villageName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String fruitsId;
        /** Ignored when Seva Sindhu supplied a mobile number (that one is used). */
        private String mobileNumber;
        private String passbookNumber;
        private String bankName;
        private String bankBranchName;
        private String ifscCode;
        private String accountNumber;
        private Long districtId;
        private Long talukId;
        private Long hobliId;
        private Long villageId;
        private String addressText;
        private String pincode;
        private Boolean consent;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Option {
        private Long id;
        private String name;
        private String nameInKannada;
    }
}
