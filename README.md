
## Smart Portfolio - 스마트 포트폴리오

## 대표 이미지
<p orientaion=horizontal align=center>
  <img src=img/app-image/Login.jpg height=500>
  <img src=img/app-image/Home.jpg height=500>
  <br>
  <img src=img/app-image/Timeline.jpg height=500>
  <img src=img/app-image/Messenger.jpg height=500>
  <img src=img/app-image/Card.jpg height=500>
</p>

## 개요
+ 더존비즈온 산학 연계 교과목인 2022-2 모바일 프로그래밍 수업 기말 프로젝트이다.
+ 팀원들의 포트폴리오를 관리 및 제공하는 어플 개발을 목표로 한다.

---

## 사용 기술
+ Kotlin, SQLite, Git, Lottie ...(기타 코틀린 Layout)
---

## 개발 목표
+ 더존비즈온에서 제공한 개발 요구사항 문서를 읽고, 해당 기능들을 구현한다.
---

## 개발 현황
  ### 1. 개발
    1. DrawerLayout 사용하여 슬라이드 메뉴 제공
    2. Toolbar 사용해 사용자마다 이름과 프로필 이미지가 해당 유저 페이지일 때 액션바에 보여지게 함
    3. TabLayout을 사용해 보다 빠르게 홈화면 이동 및 포트폴리오 추가 / 삭제가 가능하게 함
    4. ViewPager를 이용해 유저들 포트폴리오 보여줌
    5. 커스텀 다이얼로그를 이용해 불필요한 액티비티를 생성하기보다 간단한 데이터는 다이얼로그를 통해 해결함
    6. WebView를 통해 링크 존재 시 내부 브라우저 WebView 보여줌
    
  ### 2. 디자인
    1. DrawerLayout 구현
    2. Toolbar 구현
    3. Custom EditText(라운드) 구현
    4. Custom RoundButton 구현
    5. App Theme 수정(메인 컬러 추가 및 강조, EditText 색상 변경)
    6. 앱 이미지 제작 및 반영
    7. Lottie 로딩 화면 구현
    8. Toolbar에 유저 이미지 및 이름 추가
    9. 데이터 추가 및 삭제, 저장과 관련해 유저에게 다이얼로그로 동작 재확인 함
    10. CardView와 ImageView로 다양한 유형의 포트폴리오 뷰 제공
---

## 개선 사항
+ Code Refactoring
+ Log 관리
+ Latency 최소화
