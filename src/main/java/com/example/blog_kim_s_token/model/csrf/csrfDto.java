package com.example.blog_kim_s_token.model.csrf;

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
@Data
@Builder
@Table(name="csrftoken")
@Entity
public class csrfDto {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="userId",nullable = false)
    private int userId;
   
    @Column(name="email",nullable = false)
    private String email;

    @Column(name="csrfToken",nullable = false)
    private String csrfToken;

    @Column(name="created")
    @CreationTimestamp
    private Timestamp created;
}
