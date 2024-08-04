# 변경사항 및 유니티 연결시 수정사항 기록


# 유니티 연결하는법
    ## app -> 우클릭 -> module -> 좌측 하단 import -> unityLibrary폴더 import
    
    ## settings.gradle 삭제(모듈 추가시 자동 생성됨)
    
    ## settings.gradle.kts파일에 최하단에 아래 두 줄 추가
    ### include(":unityLibrary")
    ### include(":unityLibrary:xrmanifest.androidlib")
    
    ## unityLibrary폴더의 build.gradle 수정
    ### implementation은 아래와 같이 수정
    #### implementation(name: 'arcore_client', ext:'aar') -> implementation files("libs/arcore_client.aar")
    
    ### build.gradle에 namespace 추가
    #### unity Library
    ##### android { namespace "com.unity3d.player"
    #### xrmanifest
    ##### android { namespace "com.UnityTechnologies.XR.Manifest"
    
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


# 유니티와 데이터 주고 받기(ver.20240604)
    ## Unity에서 데이터 받기 (Unity 코드)
    ### AndroidJavaClass androidJavaClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
    ### AndroidJavaObject androidJavaObject = androidJavaClass.GetStatic<AndroidJavaObject>("currentActivity");
    ### string coordinate = androidJavaObject.Call<string>("GetCoordinate");
    #### 위 코드에서 GetCoordinate는 UnityPlayerActivity에 있는 함수명(필요한 함수 새로 만들어서 사용)

    ### UnityActivity에서 Intent를 통해 Unity로 전달 (UnityPlayerActivity 코드)
    #### return getIntent().getStringExtra("result");

    ## Unity에서 데이터 보내기 (Unity 코드) <- 추후 구현시 작성
    ###

    ### UnityPlayerActivity에서 Intent를 통해 UnityActivity로 전달 (UnityPlayerActivity 코드)
    #### public void SendToastFromUnity(String text){
        Log.d("come_text", text);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", text);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    #### UnityActivity에서 처리하는 코드는 파일 확인할 것
    

# Retrofit 파일 주의사항(ver.20240525)
    ## pathfinging 하위 폴더로 service폴더 추가
    ### service 폴더는 구글 드라이브에 있음


# beaconmanager 변경사항
    ## bind -> bindInternal
    ## unbind -> unbindInternal
    ## startRangingBeaconsInRegion -> startRangingBeacons


# Application이 2개 설치되는 오류 해결
    ## unityLibrary 모듈의 AndroidManifest.xml 파일에서 intent-filter 구문 삭제


# 전체 변경 및 추가사항(ver.20240525)
    ## beacon 탐색 버튼 추가 (클릭시 탐색 활성화, 재 클릭시 비활성화)
    ## text 갱신 버튼 변경 (beacon탐색 기능 삭제)
    ## beacon 탐색 주기 설정 (2초)
    ## ---------------------20240525--------------------------
    ## 기능별 폴더로 묶음
    ### beaconfind -> Beacon 탐색 부분 모음
    ### pathfinding -> 경로 탐색 기능 모음
    ## Retrofit 기능 추가
    ## ---------------------20240713(밀린 업뎃)--------------------------
    ## beacon 탐색 주기 변경 (1초)
    ## 비콘 탐색시 로딩 기능 추가
    ### 탐색 성공 시 PathFindingActivity를 통해 UnityActivity 실행 (좌표 전송)
    ## Splash 창에서 권한 부여
    ### 권한 부여 전에 화면 전환되는 오류 수정
    ### 블루투스 상태를 비콘 탐색 전 확인하도록 변경
    ## ---------------------20240805--------------------------
    ## 권한 부여 거부시에만 Dialog가 뜨게 변경 

