package com.sericulture.registration.model.api.farmer;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer number must contain only letters and numbers")
    @Schema(name = "farmerNumber", example = "12345")
    String farmerNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Fruits Id must contain only letters and numbers")
    @Schema(name = "fruitsId", example = "1")
    String fruitsId;

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

    @Pattern(regexp = "^[a-zA-Z0-9@.\\s]*$", message = "Email must contain only letters and numbers")
    @Schema(name = "email", example = "example@xyz.com")
    String email;

    @Pattern(regexp = "^[+0-9\\s]*$", message = "Mobile number must contain only numbers")
    @Schema(name = "mobileNumber", example = "9876543210")
    String mobileNumber;

    @Pattern(regexp = "^[0-9\\s]*$", message = "Aadhar number must contain only numbers")
    @Schema(name = "aadhaarNumber", example = "111122223333")
    String aadhaarNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Epic number must contain only letters and numbers")
    @Schema(name = "epicNumber", example = "1")
    String epicNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Ration card number must contain only letters and numbers")
    @Schema(name = "rationCardNumber", example = "12345")
    String rationCardNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Total land holding must contain only letters and numbers")
    @Schema(name = "totalLandHolding", example = "100")
    String totalLandHolding;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Passbook number must contain only letters and numbers")
    @Schema(name = "passbookNumber", example = "1001001000100")
    String passbookNumber;

    @Schema(name = "landCategoryId", example = "1")
    Long landCategoryId;

    @Schema(name = "educationId", example = "1")
    Long educationId;

    @Schema(name = "representativeId", example = "1")
    Long representativeId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Khazane receipt must contain only letters and numbers")
    @Schema(name = "khazaneRecipientId", example = "1")
    String khazaneRecipientId;

    @Pattern(regexp = "^[a-zA-Z0-9-/_.\\s]*$", message = "Photopath must contain only letters and numbers")
    @Schema(name = "photoPath", example = "/example.jpg")
    String photoPath;

    @Schema(name = "farmerTypeId", example = "1")
    private Long farmerTypeId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Minority must contain only letters and numbers")
    @Schema(name = "minority", example = "test")
    private String minority;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Rd Number must contain only letters and numbers")
    @Schema(name = "rdNumber", example = "12097")
    private String rdNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Caste status must contain only letters and numbers")
    @Schema(name = "casteStatus", example = "Declared")
    private String casteStatus;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Gender status must contain only letters and numbers")
    @Schema(name = "genderStatus", example = "Declared")
    private String genderStatus;

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
}