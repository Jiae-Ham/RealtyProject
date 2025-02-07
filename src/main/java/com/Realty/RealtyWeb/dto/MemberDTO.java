package com.Realty.RealtyWeb.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

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
}
