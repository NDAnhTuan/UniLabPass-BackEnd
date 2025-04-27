package com.example.UniLabPass.repository;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface LabMemberRepository extends JpaRepository<LabMember, LabMemberKey> {
    List<LabMember> findAllByLabMemberId_LabId(String labId);

    List<LabMember> findAllByLabMemberId_LabIdAndRole(String labId, Role role);

    List<LabMember> findAllByLabMemberId_MyUserId(String userId);

    // Vì đây là thao tác thay đổi db và được custom nên phải cần annotation
    @Transactional
    void deleteByLabMemberId_LabId(String labId);

    @Transactional
    void deleteByLabMemberId_MyUserId(String userId);
}
