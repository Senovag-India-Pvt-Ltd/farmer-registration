package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InspectionTask extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inspection_task_seq")
    @SequenceGenerator(name = "inspection_task_seq", sequenceName = "inspection_task_seq", allocationSize = 1)
    @Column(name = "inspection_task_id")
    private Long inspectionTaskId;

    @Column(name = "inspection_type")
    private Long inspectionType;

    @Column(name = "inspector_id")
    private Long userMasterId;

    @Column(name = "status")
    private Integer status;


    @Column(name = "inspection_date")
    private LocalDate inspectionDate;

    @Column(name = "inspected_date")
    private LocalDate inspectedDate;


    @Column(name = "request_type")
    private String requestType;

    @Column(name = "request_type_id")
    private Long requestTypeId;


}
