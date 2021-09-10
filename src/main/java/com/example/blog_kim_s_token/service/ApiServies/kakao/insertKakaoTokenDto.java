package com.example.blog_kim_s_token.service.ApiServies.kakao;

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
@Table(name = "kakaologintoken")
@Entity
public class insertKakaoTokenDto {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="email",nullable = false)
    private String email;

    @Column(name="accessToken",nullable = false)
    private String accessToken;

    @Column(name="refreshToken",nullable = false)
    private String refreshToken;

    @Column(name = "created")
    @CreationTimestamp
    private Timestamp created;

}
