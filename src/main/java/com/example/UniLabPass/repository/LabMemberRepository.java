package com.example.UniLabPass.repository;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.LabMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabMemberRepository extends JpaRepository<LabMember, LabMemberKey> {
}
