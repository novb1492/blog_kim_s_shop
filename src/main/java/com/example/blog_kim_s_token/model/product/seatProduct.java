package com.example.blog_kim_s_token.model.product;

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

@AllArgsConstructor
@Builder
@Data
@Table(name="seatproduct")
@Entity
public class seatProduct {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "seatName",nullable = false)
    private String seatName;
    
    @Column(name = "seatPrice",nullable = false)
    private int seatPrice;

    @Column(name = "adminId",nullable = false)
    private int adminId;

    @Column(name = "adminEmail",nullable = false)
    private String adminEmail;

    @Column(name = "adminName",nullable = false)
    private String adminName;
    
    @Column(name="created")
    @CreationTimestamp  
    private Timestamp created;
}
