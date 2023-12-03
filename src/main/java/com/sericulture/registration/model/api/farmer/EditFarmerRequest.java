package com.sericulture.registration.model.api.farmer;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EditFarmerRequest extends RequestBody {
    @Schema(name = "farmerId", example = "1")
    Long farmerId;

    @Schema(name = "farmerNumber", example = "12345")
    String farmerNumber;

    @Schema(name = "fruitsId", example = "1")
    String fruitsId;

    @Schema(name = "firstName", example = "First name")
    String firstName;

    @Schema(name = "middleName", example = "Middle name")
    String middleName;

    @Schema(name = "lastName", example = "Last name")
    String lastName;

    @Schema(name = "dob", example = "2023-11-03 16:27:35.907")
    Date dob;

    @Schema(name = "genderId", example = "1")
    Long genderId;

    @Schema(name = "casteId", example = "1")
    Long casteId;

    @Schema(name = "differentlyAbled", example = "0")
    Boolean differentlyAbled;

    @Schema(name = "email", example = "example@xyz.com")
    String email;

    @Schema(name = "mobileNumber", example = "9876543210")
    String mobileNumber;

    @Schema(name = "aadhaarNumber", example = "111122223333")
    String aadhaarNumber;

    @Schema(name = "epicNumber", example = "1")
    String epicNumber;

    @Schema(name = "rationCardNumber", example = "12345")
    String rationCardNumber;

    @Schema(name = "totalLandHolding", example = "100")
    String totalLandHolding;

    @Schema(name = "passbookNumber", example = "1001001000100")
    String passbookNumber;

    @Schema(name = "landCategoryId", example = "1")
    Long landCategoryId;

    @Schema(name = "educationId", example = "1")
    Long educationId;

    @Schema(name = "representativeId", example = "1")
    Long representativeId;

    @Schema(name = "khazaneRecipientId", example = "1")
    String khazaneRecipientId;

    @Schema(name = "photoPath", example = "/example.jpg")
    String photoPath;

    @Schema(name = "farmerTypeId", example = "1")
    private Long farmerTypeId;

    @Schema(name = "minority", example = "test")
    private String minority;

    @Schema(name = "rdNumber", example = "12097")
    private String rdNumber;

    @Schema(name = "casteStatus", example = "Declared")
    private String casteStatus;

    @Schema(name = "genderStatus", example = "Declared")
    private String genderStatus;

    @Schema(name = "fatherNameKan", example = "test")
    private String fatherNameKan;

    @Schema(name = "fatherName", example = "test")
    private String fatherName;

    @Schema(name = "nameKan", example = "test")
    private String nameKan;
}