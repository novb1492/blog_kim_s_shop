package com.example.blog_kim_s_token.service.payment.model.tempPaid;

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
@Table(name = "temppaid")
@Entity
public class tempPaidDto {
    
    @Id
    @Column(name="tpid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tpid;

    @Column(name="tpPaymentId",nullable = false)
    private String tpPaymentid;

    @Column(name = "tpEmail",nullable = false)
    private String tpEmail;

    @Column(name = "tpPrice",nullable = false)
    private String tpPrice;

    @Column(name="tpcreated")
    @CreationTimestamp  
    private Timestamp trcreated;
}
