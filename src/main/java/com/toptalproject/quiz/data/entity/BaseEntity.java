package com.toptalproject.quiz.data.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The common information to store in db for all entities to enable audit.
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  public static final long serialVersionUID = 43287434328743L;
  /**
   * The unique id of each entity/row.
   */
  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;

  /**
   * The version.
   */
  @Version
  private Integer version;

  /**
   * The created at info.
   */
  @Column(name = "created_at")
  @CreatedDate
  private LocalDateTime createdAt;

  /**
   * The created by info.
   */
  @CreatedBy
  @Column(name = "created_by")
  private String createdBy;

  /**
   * The updated at info.
   */
  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  /**
   * The updated by info.
   */
  @LastModifiedBy
  @Column(name = "updated_by")
  private String updatedBy;
}
