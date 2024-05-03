# 변경사항 및 유니티 연결시 수정사항 기록


# 유니티 연결하는법
## app -> 우클릭 -> module -> 좌측 하단 import -> unityLibrary폴더 import


## settings.gradle.kts파일에 최하단에 아래 두 줄 추가
### include(":unityLibrary")
### include(":unityLibrary:xrmanifest.androidlib")


## unityLibrary폴더의 build.gradle 수정
### implementation은 아래와 같이 수정
#### implementation(name: 'arcore_client', ext:'aar') -> implementation files("libs/arcore_client.aar")

### namespace 추가
#### android { namespace "com.unity3d.player"

### compileOptions에서 JavaVersion 수정 
#### JavaVersion.VERSION_11 -> JavaVersion.VERSION_1_8


## 모든 build.gradle 수정사항
### compileSdkVersion 및 targetSdkVersion -> 34
### buildToolsVersion '32.0.0' -> '34.0.0'

## xrmanifest.androidlib 수정사항
### AndroidManifest.xml 수정
#### <application> </application>까지 주석


## unityLibrary를 모듈로 추가
### app -> 우클릭 -> Open Module Setting -> Dependencies -> app 클릭 후 나오는 오른쪽 Dependency 상단 + 클릭
### -> new Module Dependency -> unityLibrary 선택 


## 폰에서 unity 실행이 안될때
### res -> values -> strings.xml에 한 줄 추가
#### <string name="game_view_content_description" translatable="false">Game View</string>


## unityStreamingAssets오류 발생시
### gradle.properties 파일에 한줄 추가
#### unityStreamingAssets=.unity3d, google-services-desktop.json, google-services.json, GoogleService-Info.plist

