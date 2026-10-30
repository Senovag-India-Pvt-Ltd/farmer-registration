package com.sericulture.registration.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Audit trail of a farmer's own details: the values imported from FRUITS when the farmer registered through
 * Seva Sindhu (change_type FRUITS_IMPORT) and every later field change made in the Farmer Registration module
 * (change_type EDIT), with who changed it and when. Table: db-scripts/ddl/farmer_change_history.sql
 */
@Entity(name = "farmer_change_history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FarmerChangeHistory implements Serializable {

    public static final String TYPE_FRUITS_IMPORT = "FRUITS_IMPORT";
    public static final String TYPE_EDIT = "EDIT";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "farmer_change_history_seq")
    @SequenceGenerator(name = "farmer_change_history_seq", sequenceName = "farmer_change_history_seq", allocationSize = 1)
    @Column(name = "farmer_change_history_id")
    private Long id;

    @Column(name = "farmer_id")
    private Long farmerId;

    @Column(name = "change_type")
    private String changeType;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "changed_date")
    private Date changedDate;
}
