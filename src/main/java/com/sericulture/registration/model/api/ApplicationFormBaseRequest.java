package com.sericulture.registration.model.api;

import lombok.*;
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ApplicationFormBaseRequest extends RequestBody{


        private int headOfAccountId;

        private int schemeId;

        private int subSchemeId;

        private int categoryId;


}
