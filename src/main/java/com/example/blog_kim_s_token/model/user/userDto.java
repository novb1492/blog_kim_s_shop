package com.example.blog_kim_s_token.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
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

    @Column(name = "pwd",nullable = false,length = 100)
    private String pwd;

    @Column(name="role",nullable = false,length = 10)
    private String role;

    @Column(name="postcode",nullable = false,length = 10)
    private String postCode;

    @Column(name="address",nullable = false,length = 40)
    private String address;

    @Column(name="detailAddress",nullable = false,length = 20)
    private String detailAddress;
    
    @Column(name="extraAddress",nullable = false,length = 10)
    private String extraAddress;
    
    @Column(name="phoneNum",nullable = false,length = 15)
    private String phoneNum;

    @Column(name="emailCheck")
    @ColumnDefault("0")
    private int emailCheck;

    @Column(name="phoneCheck")
    @ColumnDefault("1")
    private int phoneCheck;

    @Column(name="failLoginTime")
    @ColumnDefault("0")
    private int failLoginTime;

    @Column(name="failLogin")
    @ColumnDefault("0")
    private int failLogin;

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
    }

}
