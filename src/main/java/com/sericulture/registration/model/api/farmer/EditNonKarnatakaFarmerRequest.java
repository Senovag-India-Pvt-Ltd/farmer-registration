package com.sericulture.registration.model.api.farmer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sericulture.registration.model.entity.FarmerAddress;
import com.sericulture.registration.model.entity.FarmerBankAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = {JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES})
public class EditNonKarnatakaFarmerRequest {

    @Schema(name = "farmerId", example = "1")
    Long farmerId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "First name must contain only letters and numbers")
    @Schema(name = "firstName", example = "First name")
    String firstName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Middle name must contain only letters and numbers")
    @Schema(name = "middleName", example = "Middle name")
    String middleName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Last name must contain only letters and numbers")
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

    @Pattern(regexp = "^[+0-9\\s]*$", message = "Mobile number must contain only numbers")
    @Schema(name = "mobileNumber", example = "9876543210")
    String mobileNumber;



    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Epic number must contain only letters and numbers")
    @Schema(name = "epicNumber", example = "1")
    String epicNumber;



    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Passbook number must contain only letters and numbers")
    @Schema(name = "passbookNumber", example = "1001001000100")
    String passbookNumber;



    @Pattern(regexp = "^[a-zA-Z0-9-/_.\\s]*$", message = "Photopath must contain only letters and numbers")
    @Schema(name = "photoPath", example = "/example.jpg")
    String photoPath;

    @Schema(name = "farmerTypeId", example = "1")
    private Long farmerTypeId;



    @Pattern(regexp = "^[a-zA-Z0-9\\s\\u0C80-\\u0CFF]*$", message = "Father name in kannada must contain only letters and numbers")
    @Schema(name = "fatherNameKan", example = "test")
    private String fatherNameKan;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Father name must contain only letters and numbers")
    @Schema(name = "fatherName", example = "test")
    private String fatherName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s\\u0C80-\\u0CFF]*$", message = "Name in kannada must contain only letters and numbers")
    @Schema(name = "nameKan", example = "test")
    private String nameKan;

    @Schema(name = "isOtherStateFarmer", example = "0")
    private Boolean isOtherStateFarmer;

    @Schema(name = "farmerAddressList")
    List<FarmerAddress> farmerAddressList;

    @Schema(name = "farmerBankAccount")
    FarmerBankAccount farmerBankAccount;
}
