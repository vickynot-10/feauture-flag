package com.feauture.app.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "apps")
@Getter
@Setter

public class AppApplications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 1)
    private int environment;

    @Column(nullable = false)
    private boolean status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant created_at;

    @CreationTimestamp
    @Column(updatable = true)
    private Instant updated_at;

}
