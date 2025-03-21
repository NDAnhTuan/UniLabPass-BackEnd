package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.LaboratoryLog;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<LaboratoryLog, String> {
    List<LaboratoryLog> findByLabId(String labId);

    List<LaboratoryLog> findByLabIdAndUserId(String labId, String userId);

    List<LaboratoryLog> findByRecordTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Transactional
    void deleteByLabId(String labId);
//    Page<LaboratoryLog> findAll(Pageable pageable);

    Optional<LaboratoryLog> findFirstByUserIdAndLabIdOrderByRecordTimeDesc(String userId, String labId);

    List<LaboratoryLog> findByLabIdAndRecordTimeBetween(String labId, LocalDateTime recordTimeAfter, LocalDateTime recordTimeBefore);
}
