package com.example.blog_kim_s_token.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name="detailaddress",nullable = false,length = 20)
    private String detailAddress;
    
    @Column(name="extraaddress",nullable = false,length = 10)
    private String extraAddress;
    
    @Column(name="phoneNum",nullable = false,length = 15)
    private String phoneNum;
}
