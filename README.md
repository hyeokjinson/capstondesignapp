Firebase Cloud Messaging & person identify
==============================

The Firebase Cloud Messaging Android Quickstart app demonstrates registering
an Android app for notifications and handling the receipt of a message.
**InstanceID** allows easy registration while **FirebaseMessagingService** and **FirebaseInstanceIDService**
enable token refreshes and message handling on the client.

Introduction
------------

-3.3 Android App 기본 GUI 구성도
구현 환경 : Android Studio 3.5 Version

< APP UI >
3.3.1 Android App 기능 별 시퀸스 다이어그램

			
App의 구현 xml 화면별 기능은 다음과 같다.
● 초기 부팅 화면 : 앱 클릭 시 초기 부팅화면에 들어가고 앱설치후 첫 실행 시 보안토큰이 발급
● 이벤트 처리 시 보안토큰값이 일치해야 서버에 메시지 전송 가능


<인증 과정 설계도>
● 메인화면
메인화면에 진입 시 실시간 카메라 스트리밍 뷰어가 보이며 영상 송출이 된다.
(1) 카메라 모듈 스트리밍 뷰어
메인화면 진입 시 실시간 영상 스트리밍 화면을 보여주게 된다.
(2) 사이드 바 메뉴
■ 차량 출차
차량 출차 버튼 클릭 할 수 있는 GUI와 출차가 완료되면 출차 완료된 장소를 캡처해서 보여주게 된다.

■ 차량 주차
<차량 주차 버튼>
차량 주차 버튼 클릭 할 수 있는 GUI와 차량 주차 완료 시 이미지 캡처를 통해 완료된 것을 보여주게 된다.
■ 영상 송출
웹 서버에 연결하여 실시간 영상 송출 화면을 볼 수 있다.

3.3.2 Android App 기능 별 라즈베리 파이 통신 방식
(1) 카메라 모듈 스트리밍 뷰어

<영상 송출 통신 원리>
안드로이드에서 작동 시킬 특정 장비에 on/off의 플래그 값을 서버로 전송하게 된다. 서버는 사용자가 전송한 값을 받아서 해당하는 카메라 모듈 스트리밍 서버를 실행하게 되고 안드로이드 앱은 WebView를 통해 해당 IP/Port로 들어가서 실시간 영상 스트리밍을 볼 수 있다.다른 이벤트들을 수행하기 위해 버튼과 기능별로 스레드를 생성하여 이벤트들을 처리한다.
(2) 버튼 별 터치 시 통신 원리
안드로이드(스마트폰)폰 과 라즈베리파이와의 통신을 하는 서버 클라이언트 개념으로 이루어 질 것이다. 통신 방식은 소켓 통신 방식을 채택할 것 이다.

<소켓 통신 원리>
여기서 TCP/IP 연결지향형 소켓통신을 통해 통신하기 때문에 연결된 대상 외에 다른 대상과는 통신이 불가능하게 한다. 앱과 서버와 연결을 하기 전에 START 버튼 클릭 시 인증을 거치게 되는데 사용자의 SSID or IMEI 하드웨어 자체의 시리얼 넘버 값을 해쉬함수로 넘기게 되어 인증을 거치게 된다. 인증을 마친 후 메인화면에 접속이 되고 기능들을 수행 할 수 있다.
 

Getting Started
---------------

- 
