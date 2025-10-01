# Schedule Command Service

스케줄 도메인의 명령 서비스 (커스텀 일정 생성/수정/삭제 + 외부 도메인(Study) 이벤트 기반 일정 동기화)

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

처리 규칙
- Registered → 일정 생성 또는 업서트
- Cancelled/Completed → 일정 삭제
- scheduleId 결정: UUID.nameUUIDFromBytes("study:"+meetingId+":"+userId)

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
  - Header: `X-Owner-Id: <UUID>`
  - Body: { title, description?, startAt, endAt }
- PATCH `/schedules/custom` — 일정 수정
  - Header: `X-Owner-Id: <UUID>`
  - Body: { scheduleId, title, description?, startAt, endAt }
- DELETE `/schedules/custom` — 일정 삭제
  - Header: `X-Owner-Id: <UUID>`
  - Body: { scheduleId }

## 🏗️ 아키텍처
- CQRS Command Side(쓰기 전용)
- Event-Driven Architecture(Kafka 기반 이벤트 수신/발행)
- Event Listener Pattern(Study 도메인 이벤트 구독)
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
