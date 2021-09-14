package com.example.blog_kim_s_token.model.reservation;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name="tempreservation")
@Entity
public class tempReservationDto {
    
    @Id
    @Column(name="trid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int trid;

    @Column(name="trseat",nullable = false)
    private String trseat;

    @Column(name = "tremail",nullable = false)
    private String tremail;

    @Column(name = "trname",nullable = false)
    private String trname;

    @Column(name="trtime")
    private int trtime;

    @Column(name="trpaymentId")
    private String trpaymentId;

    @Column(name = "trstatus",nullable = false)
    private String trstatus;

    @Column(name = "trusedPayKind",nullable = false)
    private String trusedPayKind;

    @Column(name="trrDate")
    private Timestamp trrDate;

    @Column(name="trdateAndTime")
    private Timestamp trdateAndTime;

    @Column(name="trcreated")
    @CreationTimestamp  
    private Timestamp trcreated;

}
