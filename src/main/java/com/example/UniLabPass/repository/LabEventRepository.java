package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.LabEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabEventRepository extends JpaRepository<LabEvent, String> {
    List<LabEvent> findAllByLabId(String labId);
}
