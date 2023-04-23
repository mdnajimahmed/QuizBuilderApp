package com.toptalproject.quiz.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BaseEntity implements Serializable {
  public static final long serialVersionUID = 43287434328743L;
  @Id
  @GeneratedValue
  @UuidGenerator
  @EqualsAndHashCode.Include
  private UUID id;
  @Version
  private Integer version;
  @Column(name = "created_at")
  @CreatedDate
  private LocalDateTime createdAt;
  @CreatedBy
  @Column(name = "created_by")
  private String createdBy;
  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;
  @LastModifiedBy
  @Column(name = "updated_by")
  private String updatedBy;
}
