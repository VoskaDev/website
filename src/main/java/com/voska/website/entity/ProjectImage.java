package com.voska.website.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "project_images")
public class ProjectImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @Column(nullable = false)
    private String imageUrl;

    private String altText;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean cover;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
