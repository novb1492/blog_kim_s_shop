package com.example.blog_kim_s_token.config;

import java.util.ArrayList;
import java.util.Collection;

import com.example.blog_kim_s_token.model.user.userDto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class principaldetail implements UserDetails {
    
   private userDto userDto;

   public principaldetail(userDto userDto){
       this.userDto=userDto;
   }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority>collet=new ArrayList<>();
        collet.add(new GrantedAuthority(){
            @Override
            public String getAuthority() {
                System.out.println(userDto.getRole()+"권한 가져오기");
                return userDto.getRole();
            }
        });
        return collet;
    }
    @Override
    public String getPassword() {
        return userDto.getPwd();
    }
    @Override
    public String getUsername() {
        return userDto.getEmail();
    }
    @Override
    public boolean isAccountNonExpired() {  
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {  
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
