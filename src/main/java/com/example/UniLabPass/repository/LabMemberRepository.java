package com.example.UniLabPass.repository;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LabMemberRepository extends JpaRepository<LabMember, LabMemberKey> {
    List<LabMember> findByLabMemberId_LabId(String labId);

    List<LabMember> findByLabMemberId_MyUserId(String userId);

    @Transactional
    void deleteByLabMemberId_LabId(String labId);

    @Transactional
    void deleteByLabMemberId_MyUserId(String userId);
}
