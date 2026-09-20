package com.supportticket.persistence;

import com.supportticket.domain.TicketStatus;
import com.supportticket.persistence.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    Page<TicketEntity> findByStatus(TicketStatus status, Pageable pageable);

    @Query("""
            SELECT t FROM TicketEntity t
            WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(t.assignee.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(CAST(t.priority AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<TicketEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT t FROM TicketEntity t
            WHERE t.status = :status
              AND (
                LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.assignee.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(CAST(t.priority AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<TicketEntity> searchByStatusAndKeyword(
            @Param("status") TicketStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);
}
