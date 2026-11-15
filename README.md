# 아가랑

> 현대자동차그룹 차량용 앱 개발 공모전 출품작 · Pleos Connect(Android Automotive OS) 차량 앱

## 1. 앱 이름 및 한 줄 소개

- **앱 이름**: 아가랑
- **한 줄 소개**: 아기가 울기 전에 알려주는 패밀리카 케어 코파일럿 — 아기 상태 예보로 출발과 정차를 설계합니다

## 2. 실행 방법

### 2.1 APK 설치·실행

Pleos Connect 에뮬레이터를 켠 뒤 설치합니다. 서버 없이 동작합니다.

```bash
adb install -r apk/아가랑_아가랑_release.apk
adb shell am start -n com.hmgchallenge.app/.MainActivity
```

### 2.2 사용 가이드

| 화면 | 할 수 있는 일 |
| --- | --- |
| **출발 예보** (홈) | 돌봄 기록과 내비 도착 예정 시각으로 다음 수유·돌봄이 도착 전에 겹치는지 예보합니다. "어떻게 출발할까요?"에서 지금 출발·돌봄 먼저·중간 정차를 고르고, 바로 돌봄 기록과 준비물 확인도 여기서 합니다. |
| **울음 대응** | 홈의 "울음 대응"을 누르면 가까운 정차지와 수유실이 있는 시설을 보여 주고, 고른 곳을 내비 경유지로 등록합니다. 울음 원인은 단정하지 않습니다. |
| **준비물** (하단 탭) | 외출 시간에 맞춘 수유용품·기저귀 수량과 체크리스트입니다. |
| **돌봄 기록** (하단 탭) | 수유·기저귀·수면을 한 번에 기록하고, 최근 기록을 바로 취소할 수 있습니다. |
| **주행 중** | 차가 움직이면 핵심 요약과 울음 대응만 보이고, 입력·설정·긴 글은 주차 후로 미룹니다. |

기록이 없는 상태에서 바로 체험하려면 오른쪽 위 **더 보기**를 엽니다(합성 데이터, 실제 기록·내비에는 반영하지 않음).

- **출발 준비 체험** → **기록 있는 여정**: 예보와 준비물이 채워진 홈을 봅니다. 이어서 **도착 90분 지연 보기**로 도착 전에 돌봄이 겹치는 상황을 봅니다.
- **울음 대응 체험**: 임박 예보, 정차 후보 없음, 내비 실패 상황을 봅니다.
- 끝내려면 **데모 끝내기**.

### 2.3 소스에서 빌드(선택)

Android Studio로 저장소를 열어 `frontend/app`을 실행하거나, 명령줄에서 빌드합니다(JDK 17 이상, Pleos 시스템 이미지 필요).

```bash
./gradlew :frontend:app:installDebug -Phmg.bundledData=true   # 서버 없이 내장 데이터로 동작하는 debug 빌드
```

## 3. 테스트 환경 및 필요한 설정

| 항목 | 값 |
| --- | --- |
| 에뮬레이터 | Pleos Connect Emulator v2.1.0 (API 34, Automotive, **Cold boot**) — [설치 안내](https://document.pleos.ai/docs/connect/guide/getting-started/application-development/setup-connect-sdk-emulator) |
| 화면 | 2560×1440, density 160. 가로(window 2개)·세로(window 1개) 모두 지원 |
| 앱 | minSdk 28 / targetSdk 36, Kotlin + Jetpack Compose |
| Pleos SDK | Vehicle SDK 2.3.2, NaviHelper SDK 3.0.5 |
| 권한 | `pleos.car.permission.CAR_ENERGY`, `NAVI_ROUTE`, `NAVI_ROUTE_SEARCH` |

필요한 설정

1. 에뮬레이터는 **Cold boot**로 켭니다(quick boot에서는 차량 데이터를 쓸 수 없음).
2. 켠 직후 **기본 내비 앱을 한 번 열어 주세요.** 그 전에는 내비가 경로 조회에 응답하지 않아 "내비 정보를 받지 못했어요"가 보일 수 있습니다. 내비를 연 뒤 앱으로 돌아와 "다시 확인"을 누르면 연결됩니다.
3. 주행 상태는 에뮬레이터 Virtual Console에서 기어 D와 속도를 주어 바꿉니다(`scripts/pleos-console.sh`).
4. 주간/야간 테마는 시스템 야간 모드를 따릅니다: `adb shell cmd uimode night yes|no`.

## 4. APK 파일 위치 또는 GitHub Release 링크

- [`apk/아가랑_아가랑_release.apk`](apk/아가랑_아가랑_release.apk) (release 빌드, 약 49MB)
- 커밋 `5404fee` 시점의 소스로 빌드했습니다. 그 뒤 main에 들어간 UI 정리는 APK에 포함되지 않았습니다.

---

글꼴 **Asta Sans**(42dot, SIL Open Font License 1.1)를 앱에 내장했습니다. 전문: [`licenses/AstaSans-OFL.txt`](licenses/AstaSans-OFL.txt)
