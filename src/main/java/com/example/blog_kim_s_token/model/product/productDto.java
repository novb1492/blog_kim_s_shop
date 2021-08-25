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
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name="product")
@Entity
public class productDto {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "productName",nullable = false)
    private String productName;
    
    @Column(name = "price",nullable = false)
    private int price;

    @Column(name = "count",nullable = false)
    private int count;

    @Column(name = "adminEmail",nullable = false)
    private String adminEmail;

    @Column(name = "adminName",nullable = false)
    private String adminName;

    @Column(name = "kind",nullable = false)
    private String kind;
    
    @Column(name="created")
    @CreationTimestamp  
    private Timestamp created;
}
