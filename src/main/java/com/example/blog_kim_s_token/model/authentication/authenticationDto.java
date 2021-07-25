package com.example.blog_kim_s_token.model.authentication;

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
@Table(name="authentication")
@Entity
public class authenticationDto {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email",length = 30)
    private String email;

    @Column(name="phoneNum",length = 15)
    private String phoneNum;

    @Column(name="emailtempnum",nullable = false,length = 6)
    private String emailTempNum;

    @Column(name="phoneTempNum",nullable = false,length = 6)
    private String phoneTempNum;

    @Column(name="emailcheck",nullable = false,length = 4)
    private String emailCheck;

    @Column(name="phonecheck",nullable = false,length = 4)
    private String phoneCheck;
}
