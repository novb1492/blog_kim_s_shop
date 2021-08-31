package com.example.blog_kim_s_token.model.article;

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
@Table(name = "board")
@Entity
public class articleDto {

    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name="title",nullable = false,length = 50)
    private String title;

    @Column(name="textarea",nullable = false,length = 2000)
    private String textarea;

    @Column(name="email",nullable = false)
    private String email;

    @Column(name="kind",nullable = false)
    private String kind;

    @Column(name="clicked",nullable = false)
    private int clicked;

    @Column(name="created",nullable = false)
    @CreationTimestamp
    private Timestamp created;
}
