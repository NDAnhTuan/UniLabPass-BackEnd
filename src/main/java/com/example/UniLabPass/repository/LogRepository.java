package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.LaboratoryLog;
import com.example.UniLabPass.enums.LogStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<LaboratoryLog, String> {
    List<LaboratoryLog> findByLabId(String labId);

    List<LaboratoryLog> findByLabIdAndUserId(String labId, String userId);

    Optional<LaboratoryLog> findFirstByUserIdAndLabIdAndStatusNotOrderByRecordTimeDesc(String userId, String labId, LogStatus status);

    @Query(value = """
        SELECT *
        FROM laboratory_log l
        WHERE l.record_type = 0
          AND l.status = 0
          AND NOT EXISTS (
              SELECT 1
              FROM laboratory_log l2
              WHERE l2.user_id = l.user_id
                AND l2.lab_id = l.lab_id
                AND l2.record_type = 1
                AND l2.record_time > l.record_time
          )
        """, nativeQuery = true)
    List<LaboratoryLog> findUncheckoutCheckins();

    @Transactional
    void deleteByLabId(String labId);
//    Page<LaboratoryLog> findAll(Pageable pageable);

    Optional<LaboratoryLog> findFirstByUserIdAndLabIdOrderByRecordTimeDesc(String userId, String labId);

    List<LaboratoryLog> findByLabIdAndRecordTimeBetween(String labId, LocalDateTime recordTimeAfter, LocalDateTime recordTimeBefore);
}
