package com.example.loginapi.domain;

import com.example.loginapi.jwt.UserDetailsToken.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@ToString
@Table(name = "member")
@NoArgsConstructor // -> 이거 때문에 에러 났었음.
@AllArgsConstructor
@Setter
@Getter
@DynamicInsert // -> ddl 에서 해당 컬럼이 null일 경우에, insert에서 제외된다!!
public class Member {
    @Id
    @Column(name = "member_Id") // memberId로 했을 때는 적용되지 않더라. 스네이크 표현형식 고정인듯.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

//    @Column(unique = true)
    private String email;

    private String name;

//    @Builder.Default()
    @ColumnDefault("'https://res.cloudinary.com/dql4ynp7j/image/upload/v1683891387/mtkc8k2miuzbawzquxt5.jpg'")
    // +++++++++++++여기에다가 RDS(Mysql) 에 default 값으로 {원하는 pic url} 지정해주어야 제대로 적용됨.
    // -> auto-ddl 로 자동 생성되었을 때, insert를 아무것도 하지 않아 null인 값을 default 값으로 설정
    // 즉, @DynamicInsert 가 있어야만 값을 받지 않은 부분에 대해 insert를 하지않고, 만약 insert로 null을 넣게 되면 @ColumnDefault 는 cannot be null 예외가 발생한다.
    private String pic;



    @JsonIgnore
    private String password;

    @CreationTimestamp
    private LocalDateTime regdate;

    @Enumerated(EnumType.STRING)
    // 엔티티 필드에 저장된 Enum 값을 문자열 형태로 데이터베이스에 저장
    private Role role;

    // Oauth 관련--------------------

//    // oauth 관련
    private String provider; //어떤 OAuth인지(google, naver 등)

    @Column(name = "provide_id")
    private String provideId; // 해당 OAuth 의 key(id)
    @Builder
    public Member(String name, String password, String email, Role role, String provider, String provideId, LocalDateTime regdate) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.provideId = provideId;
        this.regdate = regdate;
    }
}
