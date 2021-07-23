package com.example.blog_kim_s_token.config;



import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class userDetailService implements UserDetailsService {
    @Autowired
    private userDao dao;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        userDto uservo=dao.findByEmail(username);
        if(uservo==null){
           throw new UsernameNotFoundException("존재하지 않습니다");
        }
       return new principaldetail(uservo);
    }
}
