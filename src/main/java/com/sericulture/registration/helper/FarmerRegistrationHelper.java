package com.sericulture.registration.helper;

import com.sericulture.authentication.model.JwtPayloadData;
import com.sericulture.registration.model.api.RequestBody;
import com.sericulture.registration.model.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FarmerRegistrationHelper {

    @Autowired
    Util util;

    public enum activityType {
        ISSUEBIDSLIP,
        AUCTION,
        AUCTIONACCEPT;

    }

    public JwtPayloadData getAuthToken(RequestBody requestBody) {
        JwtPayloadData jwtPayloadData = Util.getTokenValues();
        if (jwtPayloadData.getUserType()!= USERTYPE.MO.getType()) {
            throw new ValidationException(String.format("expected usertype is wrong. expected user type is: %s but found %s for the user %s",  USERTYPE.MO.getType(),jwtPayloadData.getUserType(), jwtPayloadData.getUsername()));
        }
        return jwtPayloadData;
    }

    public JwtPayloadData getReelerAuthToken(RequestBody requestBody) {
        JwtPayloadData jwtPayloadData = Util.getTokenValues();
        if (jwtPayloadData.getUserType()!= USERTYPE.REELER.getType()) {
            throw new ValidationException(String.format("expected usertype is wrong. expected user type is: %s but found %s for the user %s",  USERTYPE.MO.getType(),jwtPayloadData.getUserType(), jwtPayloadData.getUsername()));
        }
        return jwtPayloadData;
    }
}
