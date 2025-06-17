package com.futurenet.cotree.auth.oauth2.handler;

import com.futurenet.cotree.auth.constant.JwtConstants;
import com.futurenet.cotree.auth.security.dto.UserPrincipal;
import com.futurenet.cotree.auth.dto.request.RefreshTokenSaveRequest;
import com.futurenet.cotree.auth.repository.RefreshTokenRepository;
import com.futurenet.cotree.auth.util.JwtUtil;
import com.futurenet.cotree.auth.util.ResponseUtil;
import com.futurenet.cotree.member.constant.IsSignupCompletedStatus;
import com.futurenet.cotree.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        Long memberId = userPrincipal.getId();
        String role = auth.getAuthority();

        String isSignupCompleted = memberRepository.getSignupStatusByMemberId(memberId);

        String accessToken = jwtUtil.createJwt("access", memberId, role, JwtConstants.ACCESS_EXPIRED);
        String refreshToken = jwtUtil.createJwt("refresh", memberId, role, JwtConstants.REFRESH_EXPIRED);

        refreshTokenRepository.saveRefreshToken(new RefreshTokenSaveRequest(refreshToken, memberId));

        response.addCookie(ResponseUtil.createCookie("Authorization", accessToken, JwtConstants.ACCESS_COOKIE_EXPIRED));
        response.addCookie(ResponseUtil.createCookie("refresh", refreshToken, JwtConstants.REFRESH_COOKIE_EXPIRED));

        if (IsSignupCompletedStatus.COMPLETED.getStatus().equals(isSignupCompleted)) {
            response.sendRedirect("https://cotree.n-e.kr/");
        } else {
            response.sendRedirect("https://cotree.n-e.kr/login/onboarding");
        }
    }
}
