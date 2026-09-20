package com.supportticket.persistence;

import com.supportticket.domain.TicketStatus;
import com.supportticket.persistence.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    @Query("""
            SELECT t FROM TicketEntity t
            WHERE (:status IS NULL OR t.status = :status)
              AND (
                :keyword IS NULL OR TRIM(:keyword) = ''
                OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<TicketEntity> search(
            @Param("keyword") String keyword,
            @Param("status") TicketStatus status,
            Pageable pageable);

}
