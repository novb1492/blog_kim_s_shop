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
@Table(name="reservation")
@Entity
public class mainReservationDto {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="seat",nullable = false)
    private String seat;

    @Column(name = "userid",nullable = false)
    private int userid;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name="time")
    private int time;

    @Column(name="paymentId")
    private String paymentId;

    @Column(name = "status",nullable = false)
    private String status;

    @Column(name = "usedPayKind",nullable = false)
    private String usedPayKind;

    @Column(name="rDate")
    private Timestamp rDate;

    @Column(name="dateAndTime")
    private Timestamp dateAndTime;

    @Column(name="created")
    @CreationTimestamp  
    private Timestamp created;
}
