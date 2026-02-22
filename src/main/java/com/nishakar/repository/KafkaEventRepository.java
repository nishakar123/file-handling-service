package com.nishakar.repository;

import com.nishakar.entity.KafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaEventRepository extends JpaRepository<KafkaEvent, Integer> {
}
