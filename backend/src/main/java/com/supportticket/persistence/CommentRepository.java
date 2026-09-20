package com.supportticket.persistence;

import com.supportticket.persistence.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByTicket_IdOrderByCreatedAtAsc(Long ticketId);
}
