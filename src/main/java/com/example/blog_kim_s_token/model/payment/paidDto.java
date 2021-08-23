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
@Table(name = "paidproduct")
@Entity
public class paidDto {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "paymentId",nullable = false)
    private String paymentId;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "status",nullable = false)
    private String status;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "payCompany",nullable = false)
    private String payCompany;

    @Column(name = "totalPrice",nullable = false)
    private int totalPrice;

    @Column(name = "kind",nullable = false)
    private String kind;

    @Column(name = "usedKind",nullable = false)
    private String usedKind;

    @Column(name = "created")
    @CreationTimestamp
    private Timestamp created;   
}
