package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Option;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, UUID> {
}
