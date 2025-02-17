package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabRepository extends JpaRepository<Lab, String> {
}
