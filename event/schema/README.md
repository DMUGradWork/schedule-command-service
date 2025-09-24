# Schedule Events (Query Side)

- Topic: `schedule.events`
- Key: `scheduleId` (UUID)
- Serialization: JSON (LocalDateTime, KST 기준)
- Event types: CREATED | UPDATED | DELETED (소스는 `source` 필드로 구분: CUSTOM/DATING/STUDY)

## Common Fields
- eventId (UUID): 이벤트 식별자
- eventType (string): CREATED | UPDATED | DELETED
- scheduleId (UUID): 스케줄 식별자(파티션 키)
- ownerId (UUID): 소유자 식별자
- source (string): CUSTOM | DATING | STUDY
- version (long): Aggregate 버전(@Version) — 순서/아이덴포턴시 보조
- occurredAt (LocalDateTime): 이벤트 발생 시각(KST 기준)

## Events

### ScheduleCreated
- 스냅샷 전체 전달
- Payload
  - title (string), description (string|null)
  - startAt (LocalDateTime), endAt (LocalDateTime)

예시
{
  "eventId": "b02c0e32-9b6c-4c3e-9a0d-8a5b8b1b1e10",
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

### ScheduleUpdated
- 패치 이후 상태 스냅샷 전체 전달(필드 구성은 ScheduleCreated와 동일)

### ScheduleDeleted
- 삭제 의도 전달(최소 페이로드)
- title/description/startAt/endAt 없음

예시
{
  "eventId": "a3b1c4de-5f67-4890-90ab-22ccdd3311ff",
  "eventType": "DELETED",
  "scheduleId": "8f8e5d5e-63d7-4a4e-9d2f-1b93b9a1e0c7",
  "ownerId": "4c9d3e20-2fda-4ab0-8631-0c2f9b8c2d11",
  "source": "CUSTOM",
  "version": 3,
  "occurredAt": "2025-09-24T16:00:00"
}

## Consumer Guidance (Query Side)
- Upsert
  - CREATED/UPDATED: `scheduleId` 기준 upsert
- Delete
  - DELETED: `scheduleId` 기준 삭제
- Ordering/Idempotency
  - 메시지 키: `scheduleId`, `version`으로 재처리 방지 및 순서 보조
