package com.example.blog_kim_s_token.model.confrim;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name="confrim")
@Entity
public class confrimDto {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email",length = 30)
    private String email;

    @Column(name="phoneNum",length = 15)
    private String phoneNum;

    @Column(name="emailtempnum",length = 6)
    private String emailTempNum;

    @Column(name="phoneTempNum",length = 6)
    private String phoneTempNum;

    @Column(name="emailcheck",length = 1)
    @ColumnDefault("0")
    private int emailCheck;

    @Column(name="phonecheck",length = 1)
    @ColumnDefault("0")
    private int phoneCheck;

    @Column(name="requesttime",nullable = false,length = 2)
    @ColumnDefault("1")
    private int requestTime;

    @Column(name="created")
    @CreationTimestamp  
    private Timestamp created;
}
