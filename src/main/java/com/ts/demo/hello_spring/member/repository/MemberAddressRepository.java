package com.ts.demo.hello_spring.member.repository;

import com.ts.demo.hello_spring.member.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {

}
