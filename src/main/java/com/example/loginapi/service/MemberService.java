package com.example.loginapi.service;

import com.example.loginapi.domain.Member;
import com.example.loginapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member findByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    }

    @Transactional
    public boolean IsPresentEmail(String email){
        Optional<Member> member= memberRepository.findByEmail(email);
        if(member.isPresent()){
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Member addMember(Member member){
        return memberRepository.save(member);
    }
}
