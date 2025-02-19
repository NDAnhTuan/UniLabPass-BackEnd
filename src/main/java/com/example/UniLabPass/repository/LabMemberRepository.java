package com.example.UniLabPass.repository;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.LabMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabMemberRepository extends JpaRepository<LabMember, LabMemberKey> {
    List<LabMember> findByLabMemberId_LabId(String labId);

    List<LabMember> findByLabMemberId_MyUserId(String userId);

    void deleteByLabMemberId_LabId(String labId);

    void deleteByLabMemberId_MyUserId(String userId);
}
