|                      설정 > 개발자옵션                       |                     시스템 UI 데모 모드                      |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://images.velog.io/images/jshme/post/02c9a25e-2abd-433e-9fab-594f2f1cc177/Screenshot_20210801-000429_Settings.jpg) | ![](https://images.velog.io/images/jshme/post/bf47e6f4-a7f0-4462-a20b-f107b524a18f/Screenshot_20210801-000434_Samsung%20Experience%20Home.jpg) |

안드로이드에서는 배터리 상태, 알림 아이콘, 시간 등 상태바와 관련된 제어를 강제로 설정할 수 있는 `데모 모드` 가 존재한다. 설정 > 개발자 옵션을 통해서도 데모 모드를 사용할 수 있지만 adb를 이용해 shell 명령어로도 시작할 수 있다. adb를 이용해 데모 모드를 이용하는 방법에 대해 알아보자.


## 1. 데모모드 활성화하기
데모 모드는 시스템 설정으로 인해 보호되어 있으므로, 아래 명령어를 통해 데모 모드를 실행하고자 하는 디바이스를 활성화시켜야 한다.
`adb shell settings put global sysui_demo_allowed 1`

## 2. 사용할 수 있는 커맨드 리스트
* aosp의 DemoMode를 사용할 수 있는 커맨드가 나열되어 있는데, 총 9가지의 상태바 기능을 핸들링 할 수 있다.  

``` java
public interface DemoMode {
...
    public static final String COMMAND_ENTER = "enter";
    public static final String COMMAND_EXIT = "exit";
    public static final String COMMAND_CLOCK = "clock";
    public static final String COMMAND_BATTERY = "battery";
    public static final String COMMAND_NETWORK = "network";
    public static final String COMMAND_BARS = "bars";
    public static final String COMMAND_STATUS = "status";
    public static final String COMMAND_NOTIFICATIONS = "notifications";
    public static final String COMMAND_VOLUME = "volume";
}
```


* broadcast intent 기반으로 규약이 정해져있기 때문에, adb shell am broadcast `com.android.systemui.demo` (action) 를 이용해서 커멘트를 실행시킬 수 있다. 

|       Command        | Subcommand |                           Argument                           |                   Description                    |
| :------------------: | :--------: | :----------------------------------------------------------: | :----------------------------------------------: |
|        enter         |            |                                                              |        데모 모드로 들어갈 수 있는 커맨드         |
|         exit         |            |                                                              | 데모 모드를 나가고, 시스템 구동 상태로 되돌린다. |
|       battery        |            |                                                              |         화면에 보이고 있는 배터리를 제어         |
|        level         |            |            배터리 percent를 조절하는 요소 (0-100)            |                                                  |
|       plugged        |            |               충전 상태를 설정 `(true, false)`               |                                                  |
|      powersave       |            |                     절전모드 설정(true)                      |                                                  |
|       network        |            |                                                              |              RSSI(신호 강도)를 제어              |
|       airplane       |            |              비행기모드 설정(show 입력 시 보임)              |                                                  |
|         wifi         |            |                          wifi 설정                           |                                                  |
|                      |   level    |             wifi의 신호강도 설정 (null 혹은 1-4)             |                                                  |
|        mobile        |            |            셀룰러 데이터 설정(show 입력 시 보임)             |                                                  |
|                      |  datatype  |           1x, 3g, 4g, e, g, h, lte, roam 설정 가능           |                                                  |
|                      |   level    |        셀룰러 데이터의 신호강도 설정 (null 혹은 1-4)         |                                                  |
| carriornetworkchange |            |       mobile 신호 세기 아이콘을 통신사 디자인으로 변경       |                                                  |
|         sims         |            |                       sim 의 숫자 설정                       |                                                  |
|         bars         |            |                                                              |    상태바의 스타일 설정 (opaque, traslucent)     |
|         mode         |            | 상태바의 스타일 설정하는 extra(option: opaque, translucent, semi-transparent) |                                                  |
|        status        |            |                                                              |             시스템 상태 아이콘 제어              |
|        volume        |            |           볼륨 상태 제어(option: silent, vibrate)            |                                                  |
|      bluetooth       |            |     블루투스 상태 제어(option: connected, disconnected)      |                                                  |
그 외, `location` `alarm` `sync` `tty` `eri` `speakerphone` `notifications` `clock` 옵션 등이 있다. 자세한 옵션은 [이 곳](https://android.googlesource.com/platform/frameworks/base/+/master/packages/SystemUI/docs/demo_mode.md)을 확인하면 된다.

## 3. 데모모드 테스트 예제 
 * 아래와 같이 입력하여 데모 모드로 진입
`adb shell am broadcast -a com.android.systemui.demo -e command enter`
<br>

* `15:00` 로 시간 설정
`adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1500`
![](https://images.velog.io/images/jshme/post/ae330122-2187-4be9-88de-dba30eea2adf/image.png)

|               시간 변경 전 (현재 시각 `12:28`)               |                      변경 후 (`15:00`)                       |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://images.velog.io/images/jshme/post/33863b7c-11d3-489e-8e8f-d5bfc2a9e8de/Screenshot_20210801-004900_Samsung%20Experience%20Home.jpg) | ![](https://images.velog.io/images/jshme/post/58a05f53-2ddc-420e-911e-8f975497c06e/Screenshot_20210801-005005_Samsung%20Experience%20Home.jpg) |

<br>
<br>

* 충전이 필요한 배터리 0% 설정
`adb shell am broadcast -a com.android.systemui.demo -e command battery -e level
0 -e plugged false`

|                         변경 전 74%                          |                          변경 후 0%                          |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://images.velog.io/images/jshme/post/18ea1a64-9a6f-4efa-a4dd-85a007aff3a0/Screenshot_20210801-005005_Samsung%20Experience%20Home.jpg) | ![](https://images.velog.io/images/jshme/post/816a4275-aa07-4176-8d92-31114f3e95f6/Screenshot_20210801-005225_Samsung%20Experience%20Home.jpg) |

<br>
<br>

* 데모모드 종료
`adb shell am broadcast -a com.android.systemui.demo -e command exit
`

<br>

그 외, 아래와 같은 명령어도 자주 쓰일 수 있다.

* 왼쪽 상단 알람 아이콘 Visibility 핸들링 
`adb shell am broadcast -a com.android.systemui.demo -e command notifications -e
visible false`

* 셀룰러모드 및 신호강도 설정
`adb shell am broadcast -a com.android.systemui.demo -e command network -e
mobile show -e datatype none -e level 4`

* wifi모드 및 신호강도 설정
`adb shell am broadcast -a com.android.systemui.demo -e command network -e
wifi show -e level 4`