package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.LaboratoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LaboratoryLog, String> {
    List<LaboratoryLog> findByLabId(String labId);

    List<LaboratoryLog> findByLabIdAndUserId(String labId, String userId);

    List<LaboratoryLog> findByRecordTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    void deleteByLabId(String labId);
//    Page<LaboratoryLog> findAll(Pageable pageable);

    LaboratoryLog findFirstByUserIdOrderByRecordTimeDesc(String userId);

    List<LaboratoryLog> findByLabIdAndRecordTimeBetween(String labId, LocalDateTime recordTimeAfter, LocalDateTime recordTimeBefore);
}
