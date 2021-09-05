package com.example.blog_kim_s_token.model.payment;

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

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "vbank")
@Entity
public class vBankDto {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "paymentId",nullable = false,unique = true)
    private String paymentId;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "price",nullable = false)
    private int price;

    @Column(name = "status",nullable = false)
    private String status;

    @Column(name = "bank",nullable = false)
    private String bank;

    @Column(name = "bankCode",nullable = false)
    private String bankCode;

    @Column(name = "kind",nullable = false)
    private String kind;

    @Column(name = "bankNum",nullable = false)
    private String bankNum;

    @Column(name = "pgName",nullable = false)
    private String pgName;

    @Column(name = "merchant_uid",nullable = false)
    private String merchant_uid;

    @Column(name = "endDateUnixTime",nullable = false)
    private String endDateUnixTime;

    @Column(name = "endDate",nullable = false)
    private Timestamp endDate; 

    @Column(name = "created")
    @CreationTimestamp
    private Timestamp created;   
}
