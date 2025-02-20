package com.example.UniLabPass.repository;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LabMemberRepository extends JpaRepository<LabMember, LabMemberKey> {
    @Query("SELECT a FROM LabMember a WHERE a.labMemberId.labId = :labId")
    Optional<LabMember> findById_LabId(@Param("labId")String labId);
}
