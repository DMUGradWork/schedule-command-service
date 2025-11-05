# Schedule Command Service

스케줄 도메인의 명령 서비스 (커스텀 일정 생성/수정/삭제 + 외부 도메인(Study, Dating) 이벤트 기반 일정 동기화)

## 🔧 환경 설정
- 포트: 8080 (기본값)
- 데이터베이스: H2 (인메모리, dev) -> MySQL(추후 변경 예정)
- Kafka: localhost:9092
- 프로필: dev

## 📡 이벤트 스키마
CQRS Command Side로서 도메인 상태 변경 시 Query Side로 이벤트 발행(KafkaTemplate 기반)

### 🔽 수신 이벤트 (Incoming Saga Events)

<details>
<summary><strong>StudyMeetingEventParticipationRegistered</strong> - Study Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/study/schema/StudyMeetingEventParticipationRegistered.java
public record StudyMeetingEventParticipationRegistered(
    UUID studyGroupId,
    UUID meetingId,
    UUID userId,
    String studyGroupName,
    String meetingName,
    String description,
    LocalDateTime startAt,
    LocalDateTime endAt,
    LocalDateTime joinedAt
) {}
```

예시 JSON:

```json
{
  "studyGroupId": "4e9b4c3a-6f12-4c1f-9b8c-b4e1f2a3c4d5",
  "meetingId": "11111111-2222-3333-4444-555555555555",
  "userId": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
  "studyGroupName": "알고리즘 스터디",
  "meetingName": "9월 1주차 모임",
  "description": "문제 풀이 공유",
  "startAt": "2025-10-01T10:00:00",
  "endAt": "2025-10-01T12:00:00",
  "joinedAt": "2025-09-30T18:05:00"
}
```
</details>

<details>
<summary><strong>StudyMeetingParticipationCancelled</strong> - Study Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/study/schema/StudyMeetingParticipationCancelled.java
public record StudyMeetingParticipationCancelled(
    UUID studyGroupId,
    UUID meetingId,
    UUID userId
) {}
```

예시 JSON:

```json
{
  "studyGroupId": "4e9b4c3a-6f12-4c1f-9b8c-b4e1f2a3c4d5",
  "meetingId": "11111111-2222-3333-4444-555555555555",
  "userId": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
}
```
</details>

<details>
<summary><strong>StudyMeetingParticipationCompleted</strong> - Study Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/study/schema/StudyMeetingParticipationCompleted.java
public record StudyMeetingParticipationCompleted(
    UUID studyGroupId,
    UUID meetingId,
    UUID userId,
    String studyGroupName,
    String meetingName,
    LocalDateTime completedAt
) {}
```

예시 JSON:

```json
{
  "studyGroupId": "4e9b4c3a-6f12-4c1f-9b8c-b4e1f2a3c4d5",
  "meetingId": "11111111-2222-3333-4444-555555555555",
  "userId": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
  "studyGroupName": "알고리즘 스터디",
  "meetingName": "9월 1주차 모임",
  "completedAt": "2025-10-01T12:10:00"
}
```
</details>

<details>
<summary><strong>StudyMeetingRescheduled</strong> - Study Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/study/schema/StudyMeetingRescheduled.java
public record StudyMeetingRescheduled(
    UUID studyGroupId,
    UUID meetingId,
    String newMeetingName,
    String newDescription,
    LocalDateTime newStartAt,
    LocalDateTime newEndAt
) {}
```
</details>

<details>
<summary><strong>StudyMeetingCancelled</strong> - Study Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/study/schema/StudyMeetingCancelled.java
public record StudyMeetingCancelled(
    UUID studyGroupId,
    UUID meetingId
) {}
```
</details>

<details>
<summary><strong>DatingMeetingParticipantJoined</strong> - Dating Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/dating/schema/DatingMeetingParticipantJoined.java
public record DatingMeetingParticipantJoined(
    UUID meetingUuid,
    UUID authUserId,
    String gender,
    String meetingTitle,
    LocalDateTime meetingDateTime,
    LocalDateTime joinedAt
) {}
```

예시 JSON:

```json
{
  "meetingUuid": "550e8400-e29b-41d4-a716-446655440000",
  "authUserId": "650e8400-e29b-41d4-a716-446655440001",
  "gender": "MALE",
  "meetingTitle": "인코딩 테스트 미팅",
  "meetingDateTime": "2025-12-01T19:00:00",
  "joinedAt": "2025-11-04T18:20:00"
}
```
</details>

<details>
<summary><strong>DatingMeetingParticipantLeft</strong> - Dating Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/dating/schema/DatingMeetingParticipantLeft.java
public record DatingMeetingParticipantLeft(
    UUID meetingUuid,
    UUID authUserId,
    String gender,
    LocalDateTime leftAt
) {}
```

예시 JSON:

```json
{
  "meetingUuid": "550e8400-e29b-41d4-a716-446655440000",
  "authUserId": "650e8400-e29b-41d4-a716-446655440001",
  "gender": "MALE",
  "leftAt": "2025-11-04T18:45:00"
}
```
</details>

<details>
<summary><strong>DatingMeetingUpdated</strong> - Dating Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/dating/schema/DatingMeetingUpdated.java
public record DatingMeetingUpdated(
    UUID meetingUuid,
    String title,
    String description,
    LocalDateTime meetingDateTime,
    String location,
    Integer maxMaleParticipants,
    Integer maxFemaleParticipants,
    Integer currentMaleParticipants,
    Integer currentFemaleParticipants,
    LocalDateTime updatedAt
) {}
```

예시 JSON:

```json
{
  "meetingUuid": "550e8400-e29b-41d4-a716-446655440000",
  "title": "수정된 제목",
  "description": "수정된 설명",
  "meetingDateTime": "2025-12-15T20:00:00",
  "location": "서울시 종로구",
  "maxMaleParticipants": 7,
  "maxFemaleParticipants": 7,
  "currentMaleParticipants": 1,
  "currentFemaleParticipants": 1,
  "updatedAt": "2025-11-04T18:23:28.061821"
}
```
</details>

<details>
<summary><strong>DatingMeetingDeleted</strong> - Dating Service에서 수신</summary>

Record 클래스:

```java
// 📁 event/listener/dating/schema/DatingMeetingDeleted.java
public record DatingMeetingDeleted(
    UUID meetingUuid,
    LocalDateTime deletedAt
) {}
```

예시 JSON:

```json
{
  "meetingUuid": "550e8400-e29b-41d4-a716-446655440000",
  "deletedAt": "2025-11-04T18:35:00"
}
```
</details>

처리 규칙

**Study 이벤트:**
- Registered → 일정 생성 또는 업서트
- Cancelled/Completed → 일정 삭제
- Rescheduled → 해당 studyGroupId와 meetingId를 가진 모든 일정 수정
- MeetingCancelled → 해당 studyGroupId와 meetingId를 가진 모든 일정 삭제
- scheduleId 결정: UUID.nameUUIDFromBytes("study:"+meetingId+":"+userId)

**Dating 이벤트:**
- ParticipantJoined → 해당 유저의 일정 생성 또는 업서트 (기본 길이: 3시간)
- ParticipantLeft → 해당 유저의 일정 삭제
- MeetingUpdated → 해당 meetingUuid를 가진 모든 참가자의 일정 수정
- MeetingDeleted → 해당 meetingUuid를 가진 모든 참가자의 일정 삭제
- scheduleId 결정: UUID.nameUUIDFromBytes("dating:"+meetingUuid+":"+userId)

### 🔼 발행 이벤트 (Outgoing Events)

<details>
<summary><strong>ScheduleCreated</strong> - Query Service로 발행</summary>

이벤트 정보
- 이벤트 타입: CREATED
- Aggregate 타입: Schedule
- 토픽/키: `schedule.events`, key=`scheduleId`

Record 클래스:

```java
// 📁 event/schema/ScheduleCreated.java
public record ScheduleCreated(
    UUID scheduleId,
    UUID ownerId,
    String title,
    String description,
    LocalDateTime startAt,
    LocalDateTime endAt,
    ScheduleSource source, // 내부 enum — 이벤트에서 사용 여부 미정
    UUID studyGroupId,  // STUDY source용 필드 (nullable)
    UUID meetingId,     // STUDY source용 필드 (nullable)
    UUID datingMeetingUuid,  // DATING source용 필드 (nullable)
    long version,
    LocalDateTime occurredAt
) {}
```

예시 JSON:

```json
{
  "eventType": "CREATED",
  "scheduleId": "8f8e5d5e-63d7-4a4e-9d2f-1b93b9a1e0c7",
  "ownerId": "4c9d3e20-2fda-4ab0-8631-0c2f9b8c2d11",
  "title": "스터디 모임",
  "description": "자료 준비",
  "startAt": "2025-10-01T10:00:00",
  "endAt": "2025-10-01T12:00:00",
  "source": "CUSTOM",
  "version": 1,
  "occurredAt": "2025-09-24T15:15:30"
}
```
</details>

<details>
<summary><strong>ScheduleUpdated</strong> - Query Service로 발행</summary>

이벤트 정보
- 이벤트 타입: UPDATED
- Aggregate 타입: Schedule
- 토픽/키: `schedule.events`, key=`scheduleId`

Record 클래스:

```java
// 📁 event/schema/ScheduleUpdated.java
public record ScheduleUpdated(
    UUID scheduleId,
    UUID ownerId,
    String title,
    String description,
    LocalDateTime startAt,
    LocalDateTime endAt,
    ScheduleSource source, // 내부 enum — 이벤트에서 사용 여부 미정
    UUID studyGroupId,  // STUDY source용 필드 (nullable)
    UUID meetingId,     // STUDY source용 필드 (nullable)
    UUID datingMeetingUuid,  // DATING source용 필드 (nullable)
    long version,
    LocalDateTime occurredAt
) {}
```

예시 JSON은 ScheduleCreated와 동일 구조입니다.
</details>

<details>
<summary><strong>ScheduleDeleted</strong> - Query Service로 발행</summary>

이벤트 정보
- 이벤트 타입: DELETED
- Aggregate 타입: Schedule
- 토픽/키: `schedule.events`, key=`scheduleId`

Record 클래스:

```java
// 📁 event/schema/ScheduleDeleted.java
public record ScheduleDeleted(
    UUID scheduleId,
    UUID ownerId,
    ScheduleSource source, // 내부 enum — 이벤트에서 사용 여부 미정
    long version,
    LocalDateTime occurredAt
) {}
```

예시 JSON:

```json
{
  "eventType": "DELETED",
  "scheduleId": "8f8e5d5e-63d7-4a4e-9d2f-1b93b9a1e0c7",
  "ownerId": "4c9d3e20-2fda-4ab0-8631-0c2f9b8c2d11",
  "source": "CUSTOM",
  "version": 3,
  "occurredAt": "2025-09-24T16:00:00"
}
```
</details>

## 🚀 실행

```bash
./gradlew bootRun
```

## 📋 API 엔드포인트

### 커스텀 일정 관리
- POST `/schedules/custom` — 새 일정 생성
  - Header: `X-User-Id: <UUID>`
  - Body: { title, description?, startAt, endAt }
- PATCH `/schedules/custom` — 일정 수정
  - Header: `X-User-Id: <UUID>`
  - Body: { scheduleId, title, description?, startAt, endAt }
- DELETE `/schedules/custom` — 일정 삭제
  - Header: `X-User-Id: <UUID>`
  - Body: { scheduleId }

## 🏗️ 아키텍처
- CQRS Command Side(쓰기 전용)
- Event-Driven Architecture(Kafka 기반 이벤트 수신/발행)
- Event Listener Pattern(Study, Dating 도메인 이벤트 구독)
- JSON 직렬화(ISO‑8601 문자열, 타입 헤더 제거)

## 🔒 핵심 제약사항
- Command Side는 복잡한 조회 제공 안 함(Query Side로 위임)
- 멱등성: 참가 이벤트는 결정적 scheduleId로 업서트/삭제
- 운영 시 Outbox/Debezium 전환 검토(현재는 KafkaTemplate 직접 발행)

## 📊 도메인 모델
- Schedule
  - 식별자: scheduleId(UUID), ownerId(UUID)
  - 내용: title, description?, startAt, endAt
  - 분류: source(CUSTOM/DATING/STUDY), version(JPA @Version)

## 🛠️ 기술 스택
- Java 21, Spring Boot 3
- Spring Web, Spring Data JPA(H2 dev), Spring Kafka
- Lombok(제한: @Getter, @RequiredArgsConstructor)
- Gradle
