package com.example.blog_kim_s_token.model.user;

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
@Data
@Builder
@Table(name="user")
@Entity
public class userDto {
    
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email",nullable = false,unique = true,length = 30)
    private String email;

    @Column(name = "name",nullable = false,length = 20)
    private String name;

    @Column(name = "pwd",nullable = false,length = 200)
    private String pwd;

    @Column(name="role",nullable = false,length = 10)
    private String role;

    @Column(name="postcode",nullable = false,length = 10)
    private String postCode;

    @Column(name="address",nullable = false,length = 40)
    private String address;

    @Column(name="detailAddress",nullable = false,length = 20)
    private String detailAddress;
    
    @Column(name="extraAddress",length = 10)
    private String extraAddress;
    
    @Column(name="phoneNum",nullable = false,length = 15)
    private String phoneNum;

    @Column(name="emailCheck")
    private int emailCheck;

    @Column(name="phoneCheck")
    private int phoneCheck;

    @Column(name="failLoginTime")
    private int failLoginTime;

    @Column(name="failLogin")
    private int failLogin;

    @Column(name="provider")
    private String provider;

    @Column(name="created")
    @CreationTimestamp
    private Timestamp created;

    public userDto(String email,String name,String pwd,String role,String postCode,String address,String detailAddress,String extraAddress,String phoneNum){
        this.email=email;
        this.name=name;
        this.pwd=pwd;
        this.role=role;
        this.postCode=postCode;
        this.address=address;
        this.detailAddress=detailAddress;
        this.extraAddress=extraAddress;
        this.phoneNum=phoneNum;
        this.phoneCheck=1;
    }


}
