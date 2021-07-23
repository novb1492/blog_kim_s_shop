package com.example.blog_kim_s_token.jwt;

import java.sql.Timestamp;
import java.util.Date;



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.blog_kim_s_token.config.principaldetail;
import com.example.blog_kim_s_token.model.jwt.jwtDao;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.utillService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class jwtService {

    @Value("${jwt.sing}")
    private String jwtSing;
    @Value("${jwt.token.name}")
    private String jwtTokenName;
    @Value("${jwt.refreshToken.validity}")
    private int refreshTokenValidity;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private jwtDao jwtDao;
    @Autowired
    private utillService utillService;

    public String getJwtToken(int id) {
        System.out.println("getJwtToken 토큰 제작시작");
        return JWT.create().withSubject(jwtTokenName).withExpiresAt(new Date(System.currentTimeMillis()+(1000*30))).withClaim("id",id).sign(Algorithm.HMAC512(jwtSing));
    }
    public String getJwtToken() {
        System.out.println("getJwtToken 리프레시 토큰 제작시작");
        return JWT.create().withSubject(jwtTokenName).withExpiresAt(new Date(System.currentTimeMillis()+(86400*refreshTokenValidity))).sign(Algorithm.HMAC512(jwtSing));
    }
    public String getNewJwtToken(jwtDto jwtDto) {
        System.out.println(jwtDto.getUserid());
        if(jwtDto.getTokenName()!=null){
            return getJwtToken(jwtDto.getUserid());
        }else{
            System.out.println("존재하지 않는 토큰");
        }
        return null;
    }
    public int onpenJwtToken(String jwtToken) {
        return JWT.require(Algorithm.HMAC512(jwtSing)).build().verify(jwtToken).getClaim("id").asInt();
    }
    public Authentication confrimAuthenticate(userDto dto) {
        principaldetail principaldetail=new principaldetail(dto);
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPwd(),principaldetail.getAuthorities()));
    }
    public Authentication makeAuthentication(userDto userDto) {
        System.out.println(userDto.getEmail()+" makeAuthentication 강제로그인");
        principaldetail principaldetail=new principaldetail(userDto);
        return new UsernamePasswordAuthenticationToken(userDto.getEmail(),userDto.getPwd(),principaldetail.getAuthorities());
    }
    public void setSecuritySession(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    public jwtDto getRefreshToken(String refreshToken) {
        System.out.println(refreshToken+" getRefreshToken 찾기");
        return jwtDao.findByTokenName(refreshToken);
    }
    public jwtDto getRefreshToken(int userid) {
        System.out.println(userid+"기존 getRefreshToken 찾기");
        return jwtDao.findByUserid(userid);
    }
    private boolean checkRefreshTokenValidity(Timestamp refreshTokenDate) {
        return utillService.checkDate(refreshTokenDate,refreshTokenValidity);
    }
    private void insertRefreshToken(String refreshToken,int userid) {
        try {
            jwtDto jwtDto=new jwtDto(0, refreshToken, userid, null);
            jwtDao.save(jwtDto);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    private void deleteRefreshToken(jwtDto jwtDto) {
        System.out.println("deleteRefreshToken 기한 만료 리프레시 토큰 제거");
        try {
            jwtDao.delete(jwtDto);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    public String getRefreshToken(jwtDto jwtDto,int userid) {
        String refreshToken="";
        if(jwtDto==null){
            refreshToken=getJwtToken();
            insertRefreshToken(refreshToken,userid);
        }else{
            if(checkRefreshTokenValidity(jwtDto.getCreated())){
                refreshToken=getJwtToken();
                deleteRefreshToken(jwtDto);
                insertRefreshToken(refreshToken, userid);
            }else{
                refreshToken=jwtDto.getTokenName();
            }
        }
        return refreshToken;
    }
}
