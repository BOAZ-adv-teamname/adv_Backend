package com.boaz.news_service;

import com.boaz.news_service.repository.MemberRepository;
import com.boaz.news_service.util.HashFunction;
import com.boaz.news_service.vo.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/*
@Slf4j
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    public void create() throws NoSuchAlgorithmException {
        String id = "user";
        String pwd = "password";
        String email = "email@email.com";
        String nick = "nickname";

        Member member = Member.builder()
                .id(id)
                .pwd(HashFunction.sha256(pwd))
                .email(email)
                .nickname(nick)
                .build();

        try {
            Member newMember = memberRepository.save(member);
            System.out.println("New Member[" + newMember.getMemberId() + " : "+ id + "] has just signed up.");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void read() {
        long memberId = 1L;
        String title = "select member(" + memberId + ") : ";
        Optional<Member> member = memberRepository.findById(memberId);
        member.ifPresent(selectMember -> System.out.println(title + selectMember));
    }

    @Test
    @Transactional
    public void update() {
        Optional<Member> member = memberRepository.findById(1L);

        member.ifPresent(selectMember -> {
            String id = "update_id";
            String nick = "update_nick";
            selectMember.setId(id);
            selectMember.setNickname(nick);
            Member newMember = memberRepository.save(selectMember);
            System.out.println("update user : " + newMember);
        });
    }

    @Test
    @Transactional
    public void delete() {
        long id = 7L;

        Optional<Member> member = memberRepository.findById(id);
        member.ifPresent(memberRepository::delete);

        Optional<Member> deleteMember = memberRepository.findById(id);
    }

    @Test
    public void login() throws NoSuchAlgorithmException {
        String id = "user";
        String pwd = "password";
        Optional<Member> loginMember = memberRepository.findByIdAndPwd(id, HashFunction.sha256(pwd));

        if (loginMember.isPresent()) {
            Member member = loginMember.get();
            System.out.println("Successfully logged in");
            member = memberRepository.save(member);
        } else {
            System.out.println("Login failed");
        }
    }

    @Test
    public void checkDuplicate() {
        String id = "minyoung";

        Optional<Member> member = memberRepository.findById(id);

        if (member.isPresent()) {
            System.out.println("[" + id + "] is duplicate ");
        } else {
            System.out.println("[" + id + "] is available");
        }
    }
}
*/