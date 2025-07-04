package com.futurenet.cotree.member.repository;

import com.futurenet.cotree.member.domain.Member;
import com.futurenet.cotree.member.dto.MemberUpdateAgeAndGenderDto;
import com.futurenet.cotree.member.dto.MemberUpdateInfoDto;
import com.futurenet.cotree.member.dto.request.OAuthSignupRequest;
import com.futurenet.cotree.member.dto.response.MemberGenderAgeResponse;
import com.futurenet.cotree.member.dto.response.MyPageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface MemberRepository {
    Member getMemberByEmail(String email);
    int saveOAuthMember(@Param("request") OAuthSignupRequest request);
    MemberGenderAgeResponse getMemberGenderAge(@Param("id") Long id);
    MyPageResponse getMyPageInfo(@Param("id") Long id);
    int updateMemberInfo(@Param("request") MemberUpdateInfoDto request);
    int updateMemberAgeAndGender(@Param("request") MemberUpdateAgeAndGenderDto request);
    int updateMemberSignupStatus(@Param("id") Long id);
    String getSignupStatusByMemberId(@Param("id") Long id);
    long getNewMemberCount(@Param("from") LocalDate from, @Param("to") LocalDate to);

}
