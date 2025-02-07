package com.Realty.RealtyWeb.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {

    @Id
    @Column(name = "userid", length = 50, nullable = false)
    private String userId;

    @Column(name = "userpw", length = 255, nullable = false)
    private String userPw;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String userName;

    @Column(name = "useremail", length = 100, unique = true)
    private String userEmail;

    @Column(name = "userphone", length = 20, unique = true)
    private String userPhone;

    @Column(name = "userimg", length = 255, unique = true)
    private String userImg;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return userPw; // UserDetails의 password 필드와 매핑
    }

    @Override
    public String getUsername() {
        return userName; // UserDetails의 username 필드와 매핑
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부(true로 설정)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부 (true로 설정)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 (true로 설정)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true로 설정)
    }
}