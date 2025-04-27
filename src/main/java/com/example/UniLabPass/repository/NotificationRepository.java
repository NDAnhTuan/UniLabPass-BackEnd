package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.LaboratoryLog;
import com.example.UniLabPass.entity.Notification;
import com.example.UniLabPass.enums.LogStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

}
