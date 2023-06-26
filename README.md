# LocalLoginApi

- 동아리 Homebrew의 웹페이지를 만들며 그 기초가 되는 LocalLogin 작업.
- 최대한의 보안을 위주로 XSS,CSRF 공격,refreshToken 제어 등을 고려.
- 자세한 설명 : <노션 페이지> 참고 -> [Notion 링크](https://continuous-catcher-118.notion.site/JWT-4e990c17ea414940b5ff144ddc1933b1)

### 맡은 부분 및 방식
- Spring Security, JWT 방식을 이용한 로그인
- RTR(Refresh Token Rotation) 방식을 이용 -> AccessToken이 만료되고 RefreshToken 이 만료되지 않았을 때, 요청시 계속 AccessToken, RefreshToken 갱신
- AccessToken 은 브라우저의 private 변수에, RefreshToken은 쿠키에 저장

### DB
- redis 를 이용한 {key : memberId , value : RefreshToken } 방식 저장 -> host 가 localhost로 저장되어 있음.
- Mysql 을 이용한 Member 저장 -> local 로 되어있음.


### Postman
- **회원가입**
  ![회원가입](https://github.com/ceginer/Homebrew_BE/assets/92140163/91e607dd-105f-4da0-93f1-b52df2a62f0e)
  </br></br>
- **로그인**
  ![로그인](https://github.com/ceginer/Homebrew_BE/assets/92140163/f0238ee9-f040-44f6-9ec6-603b50f912cc)
  </br></br>
- **로그인유지**
  ![로그인유지](https://github.com/ceginer/Homebrew_BE/assets/92140163/d9818972-8422-4f77-b7dc-8da0d81ff4d8)
  </br></br>
- **로그아웃**
  ![로그아웃](https://github.com/ceginer/Homebrew_BE/assets/92140163/0d963f19-5dda-47ba-89ca-2ee5f5b7a479)
  </br></br>

### 특이 사항
- postman을 활용해 모든 에러사항에 대한 점검완료.
</br> -> postman 공유를 원하시면 이메일 부탁드립니다.
- redis를 ttl 을 이용하여 RefreshToken 만료시간이 지날 때마다 DB에서 자동으로 사라짐.
- Bad Credential( = Id, Pw 불일치 오류) 제외하고는, 모두 spring 에서 오류 확인가능 하다
</br> -> 혹시 뜨지 않는 오류는 CustomAuthenticationEntryPoint 클래스의 authException.printStackTrace(); 를 주석해제하여 Spring IDE 내에서 볼 수 있을 것이다.
