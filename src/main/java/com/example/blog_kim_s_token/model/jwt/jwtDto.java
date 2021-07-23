package com.example.blog_kim_s_token.model.jwt;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="jwtrefreshtoken")
@Entity
public class jwtDto {
    
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="tokenname",nullable = false)
    private String tokenName;

    @Column(name = "userid",nullable = false)
    private int userid;
    
    @Column(name="created")
    @CreationTimestamp  
    private Timestamp created;
}