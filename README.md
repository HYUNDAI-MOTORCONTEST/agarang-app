# 아가랑

> 현대자동차그룹 차량용 앱 개발 공모전 출품작. Pleos Connect(Android Automotive OS)용 Kotlin 앱입니다.

## 1. 앱 이름 및 한 줄 소개

- **앱 이름**: 아가랑
- **한 줄 소개**: 아기가 울기 전에 알려주는 패밀리카 케어 코파일럿 — 아기 상태 예보로 출발과 정차를 설계합니다

주요 기능

- **출발 예보**: 돌봄 기록과 내비 도착 예정 시각으로 다음 수유·돌봄이 도착 전에 겹치는지 예보하고(여유·주의·임박), 출발 방법(지금 출발·돌봄 먼저·중간 정차) 하나를 추천합니다.
- **준비물**: 외출 시간에 맞춘 수유용품·기저귀 수량과 체크리스트.
- **울음 대응**: 아기가 울 때 가까운 정차지와 수유실이 있는 시설을 보여 주고, 고른 곳을 내비 경유지로 등록합니다(울음 원인은 단정하지 않음).
- **원탭 돌봄 기록**: 수유·기저귀·수면을 한 번에 기록하고 바로 취소할 수 있습니다.
- **운전 중 제한**: 주행 중에는 핵심 요약과 울음 대응 버튼만 크게 보이고, 입력·설정·긴 글은 주차 후로 미룹니다(CarUxRestrictions).

## 2. 실행 방법

### 2.1 APK로 바로 실행

```bash
adb devices                                  # Pleos Connect 에뮬레이터(emulator-5554 등) 확인
adb install -r apk/아가랑_아가랑_release.apk    # 제출용 release APK 설치(서버 없이 동작)
adb shell am start -n com.hmgchallenge.app/.MainActivity
```

기록이 없는 첫 화면에서 바로 기능을 보려면 오른쪽 위 **더 보기**의 체험 메뉴를 씁니다(합성 데이터, 실제 기록·내비에는 반영하지 않음).

- **출발 준비 체험** → **기록 있는 여정**: 예보·추천·준비물이 채워진 홈. 이어서 **도착 90분 지연 보기**로 "주의" 단계와 중간 정차 추천을 봅니다.
- **울음 대응 체험**: 임박 예보, 정차 후보 없음, 내비 실패 상황.
- 끝내려면 **데모 끝내기**.

### 2.2 소스에서 빌드·실행

처음 1회 준비

1. **Android Studio**를 설치하고 SDK Tools에서 **Android SDK Command-line Tools (latest)** 를 설치합니다.
2. **Pleos 시스템 이미지**: Android Studio > SDK Manager > SDK Update Sites에 `Pleos Connect System Image`를 추가하고, SDK Platforms(Show Package Details)에서 Android 34의 **Pleos Connect v2.1.0 APIs** 를 설치합니다(42dot 라이선스 동의 필요).
   - macOS(Apple Silicon): `https://nexus-playground.pleos.ai/repository/raw-releases/connect-avd/2.x/crp-arm64-sys-img.xml`
   - Windows·Linux(x86_64): `https://nexus-playground.pleos.ai/repository/raw-releases/connect-avd/2.x/crp-sys-img.xml`
   - 원문 절차: https://document.pleos.ai/docs/connect/guide/getting-started/application-development/setup-connect-sdk-emulator
3. `scripts/setup.sh --install`: 빠진 SDK 패키지 설치, AVD `Pleos_Connect_v2.1.0`(RAM 4GB, Cold boot) 생성, `local.properties` 생성. 점검만 하려면 `scripts/setup.sh --check`.

빌드·실행

```bash
scripts/pleos-emu.sh --wait                    # 에뮬레이터 Cold boot(이미 실행 중이면 건너뜀)
scripts/run-app.sh -Phmg.bundledData=true      # debug 빌드·설치·실행(서버 없이 내장 데이터 사용)
```

- 서버를 함께 쓰려면 `./gradlew :backend:bootRun`(포트 8081)을 띄우고 `scripts/run-app.sh`로 실행합니다. debug 빌드는 에뮬레이터에서 `http://10.0.2.2:8081`로 접속합니다.
- release APK: `./gradlew :frontend:app:assembleRelease`. 서명 환경변수 `HMG_RELEASE_STORE_FILE`·`HMG_RELEASE_STORE_PASSWORD`·`HMG_RELEASE_KEY_ALIAS`·`HMG_RELEASE_KEY_PASSWORD`가 모두 있을 때만 서명합니다. 서명 키는 저장소에 넣지 않습니다.
- 스크립트는 bash(macOS·Linux)이고, JDK는 `JAVA_HOME`(17+) → Android Studio 내장 JBR 순으로 찾습니다.
- Android Studio·IntelliJ로 열면 Gradle 동기화가 끝날 때 `scripts/setup.sh`가 실행되어 에뮬레이터·백엔드를 띄울 수 있습니다. 끄려면 `~/.gradle/gradle.properties`에 `hmg.autostart=false`를 넣습니다.

## 3. 테스트 환경 및 필요한 설정

| 항목 | 값 |
| --- | --- |
| 에뮬레이터 | Pleos Connect Emulator v2.1.0 (AVD `Pleos_Connect_v2.1.0`, API 34, Automotive, Cold boot, RAM 4GB) |
| 화면 규격 | 2560×1440, density 160 (공식 기기 프로필 `Pleos_Connect_v2_0`). window 2개(가로, 약 1682×1210dp)·1개(세로, 약 841×1210dp) 모두 지원 |
| 앱 SDK | minSdk 28 / compileSdk 37 / targetSdk 36 |
| Pleos SDK | Vehicle SDK `ai.pleos.playground:Vehicle:2.3.2`, NaviHelper SDK `ai.pleos.playground:NaviHelper:3.0.5` (Maven: `https://nexus-playground.pleos.ai/repository/maven-releases/`) |
| 사용 권한 | `pleos.car.permission.CAR_ENERGY`(배터리 잔량), `NAVI_ROUTE`(경로·ETA 조회), `NAVI_ROUTE_SEARCH`(정차지 경유지 등록). 운전 중 제한(CarUxRestrictions) 구독은 권한이 필요 없음 |
| 차량 API | AAOS `android.car` (Activity `distractionOptimized` 선언) |
| 빌드 도구 | JDK 17 이상, Gradle 9.8.2(wrapper), AGP 9.4.1, Kotlin 2.4.21 |
| 백엔드(선택) | Spring Boot 4.1.2, 포트 8081. release APK는 공개 시설·경로 접근성 데이터를 앱에 내장해 서버 없이 동작 |

필요한 설정

1. AVD는 **Cold boot**로 실행합니다(quick boot에서는 VHAL을 쓸 수 없음). `scripts/pleos-emu.sh`가 Cold boot로 띄웁니다.
2. **Cold boot 직후 기본 내비 앱을 한 번 먼저 열어 주세요.** 기본 내비가 처음 열리기 전에는 경로 상태 조회에 응답하지 않아 "내비 정보를 받지 못했어요"가 보일 수 있습니다. 하단 독의 내비 아이콘으로 "어디로 갈까요?" 화면을 띄운 뒤 앱으로 돌아와 "다시 확인"을 누르면 연결됩니다. 내비가 "실행이 지연되고 있습니다"에 머물면 "다시 시도"를 누릅니다.
3. 주행 상태는 Virtual Console로 바꿉니다: `scripts/pleos-console.sh` → 기어 D·속도 입력(앱이 운전 중 제한 화면으로 바뀜).
4. 주간/야간 테마는 시스템 야간 모드를 따릅니다: `adb shell cmd uimode night yes|no`.

## 4. APK 파일 위치 또는 GitHub Release 링크

- 저장소 내 `apk/` 폴더: [`apk/아가랑_아가랑_release.apk`](apk/아가랑_아가랑_release.apk) (release 빌드, v2·v3 서명, 약 49MB)
- GitHub 파일 크기 한도(100MB) 안이라 별도 Releases 링크는 쓰지 않습니다.
- 이 APK는 커밋 `5404fee` 시점의 소스로 빌드했습니다. 그 뒤 main에 들어간 UI 정리(공용 컴포넌트, 정차 중 홈 버튼 정리·추천 표시 등)는 APK에 포함되지 않았습니다.

---

## 저장소 구조

```
.
├── apk/            # 제출용 release APK
├── frontend/app/   # :frontend:app  차량 앱 (Kotlin + Jetpack Compose, 일반 Activity 앱)
├── shared/         # :shared        예보 엔진·데이터 계약 (순수 Kotlin JVM)
├── backend/        # :backend       Spring Boot(Kotlin) 서버와 공개 시설 데이터(release는 앱에 내장)
├── scripts/        # 에뮬레이터·Virtual Console·앱 실행·환경 점검 스크립트
└── licenses/       # 서드파티 라이선스 전문 (Asta Sans OFL)
```

## 참고 문서

코드 주석의 근거는 아래 원문 문서를 가리킵니다. 문서가 충돌하면 위쪽을 우선합니다.

1. Pleos Connect UI 디자인 가이드 — https://document.pleos.ai/docs/connect/guide/docs-design/Introduction/intro
2. Pleos 차량 앱 기획·개발 가이드 — https://document.pleos.ai/docs/pleos-only/vehicle-app-planning-guide/intro , https://document.pleos.ai/docs/pleos-only/vehicle-app-developer-guide/intro
3. Pleos SDK 레퍼런스 — https://document.pleos.ai/docs/pleos-only/connect-sdk-pleos/
4. Google Design for Driving / Android for Cars(Pleos에 수치가 없을 때만 보조 근거) — https://developer.android.com/design/ui/cars , https://developer.android.com/docs/quality-guidelines/car-app-quality
5. AAOS 운전자 주의 분산 가이드라인 — https://source.android.com/docs/automotive/driver_distraction/guidelines

아기 상태 예보의 일반 참고 자료(의료 판단이 아니라 여정 계획용 참고 범위로만 씁니다)

- CDC 모유수유 빈도 — https://www.cdc.gov/infant-toddler-nutrition/breastfeeding/how-much-and-how-often.html
- CDC 분유수유 빈도 — https://www.cdc.gov/infant-toddler-nutrition/formula-feeding/how-much-and-how-often.html
- AAP(HealthyChildren) 카시트 장시간 이동 — https://www.healthychildren.org/English/tips-tools/ask-the-pediatrician/Pages/Is-it-safe-for-my-baby-to-travel-in-a-car-seat-a-few-hours-at-a-time.aspx
- The Lullaby Trust 카시트와 수면 — https://www.lullabytrust.org.uk/safer-sleep-advice/product-information/car-seats-and-sids
- NHS 우는 아기 달래기 — https://www.nhs.uk/baby/caring-for-a-newborn/soothing-a-crying-baby/

## 라이선스

- 글꼴 **Asta Sans** (`frontend/app/src/main/res/font/asta_sans_variable.ttf`): Copyright 2024 The Asta Sans Project Authors, SIL Open Font License 1.1. 전문은 [`licenses/AstaSans-OFL.txt`](licenses/AstaSans-OFL.txt). 출처: https://github.com/42dot/Asta-Sans (폰트 버전 1.000).
