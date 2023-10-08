package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@MappedSuperclass
public class BaseEntity {

    @Column(name = "CREATED_BY")
    @GenericGenerator(name = "uuid", strategy = "uuid2") //TODO once security is added we need to add the user if from principal
    private int createdBy;

    @GenericGenerator(name = "uuid", strategy = "uuid2") //TODO once security is added we need to add the user if from principal
    @Column(name = "MODIFIED_BY")
    private int modifiedBy;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFIED_DATE")
    private Date modifiedDate;

}
