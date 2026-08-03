package com.voska.website.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Project {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false)
 private String title;

 @Column(nullable = false,unique = true)
 private String slug;

@Column(nullable = false)
 private String summary;

@Column(columnDefinition = "TEXT")
 private String description;

 private String githubUrl;

 private String liveUrl;

 @Column(nullable = false)
 private boolean featured;

 @Enumerated(value = EnumType.STRING)
 @Column(nullable = false)
 private ProjectStatus status;

 @OneToMany(
         mappedBy = "project",
         cascade = CascadeType.ALL,
         orphanRemoval = true)
 private List<ProjectImage> images;

 @Column(nullable = false)
 private LocalDateTime createdAt;

 @Column(nullable = false)
 private LocalDateTime updatedAt;

}
