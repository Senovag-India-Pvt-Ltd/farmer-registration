package com.sericulture.registration.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestInspectionMapping extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_inspection_mapping_seq")
    @SequenceGenerator(name = "request_inspection_mapping_seq", sequenceName = "request_inspection_mapping_seq", allocationSize = 1)
    @Column(name = "request_inspection_mapping_id")
    private Long requestInspectionMappingId;


    @Size(min = 2, max = 250, message = " name should be more than 1 characters.")
    @Column(name = "request_type_name", unique = true)
    private String requestTypeName;

    @Column(name = "request_type")
    private Long requestType;


    @Column(name = "inspectionType")
    private Long inspectionType;
}
