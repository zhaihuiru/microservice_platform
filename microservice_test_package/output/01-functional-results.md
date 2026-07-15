# Test Results

- Total: 39
- Passed: 39
- Failed: 0

| ID | Category | Test | Expected | Actual | Result |
|---|---|---|---|---|---|
| F-01 | Auth | Admin login | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-02 | Auth | Register normal user 1 | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-03 | Auth | Register normal user 2 | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-04 | Auth | Normal user 1 login | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-05 | Auth | Normal user 2 login | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-06 | Authorization | Access personal API without token | HTTP 401 | HTTP 401 | PASS |
| F-07 | Authorization | Access personal API with invalid token | HTTP 401 | HTTP 401 | PASS |
| F-08 | Authorization | Normal user accesses admin API | HTTP 403 | HTTP 403 | PASS |
| F-09 | Authorization | Forged admin header cannot elevate privilege | ApiResponse.code=403 | HTTP=200, code=403 | PASS |
| F-10 | Authorization | Admin queries user list | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-11 | Content | Query work | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-12 | Cross-service | Character detail aggregates work and person | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-13 | Cross-service | Person detail aggregates work and character | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-14 | Content | Admin creates work through Gateway | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-15 | Content | Admin deletes test work through Gateway | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-20 | Interaction | Submit rating first time | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-21 | Idempotency | Repeated rating updates original record | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-22 | Idempotency | Two rating responses have the same ID | Same ID | first=3, second=3 | PASS |
| F-23 | Idempotency | Final score equals second submitted value | score=9 | score=9 | PASS |
| F-24 | Interaction | Submit vote first time | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-25 | Idempotency | Repeated vote for same topic is rejected | ApiResponse.code=400 | HTTP=200, code=400 | PASS |
| F-30 | Interaction | Create comment with cross-service work validation | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-31 | Moderation | Admin approves comment | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-32 | Interaction | Approved comment is visible | Comment list contains 4 | True | PASS |
| F-33 | Interaction | Like comment first time | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-34 | Duplicate prevention | Second action removes like | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-40 | Interaction | Create favorite folder | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-41 | Cross-service | Validate work before adding favorite | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-42 | Idempotency | Repeated favorite is rejected | ApiResponse.code=400 | HTTP=200, code=400 | PASS |
| F-50 | Chat | Create private conversation | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-51 | Chat | Send chat message first time | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-52 | Idempotency | Repeat send with same clientMessageId | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-53 | Idempotency | Repeated message returns same message ID | Same ID | first=5, second=5 | PASS |
| F-60 | Notification | Admin sends targeted notification | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-61 | Notification | User queries own notifications | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| F-70 | Database | Repeated rating keeps one row | count=1 | count=1 | PASS |
| F-71 | Database | Repeated vote keeps one row | count=1 | count=1 | PASS |
| F-72 | Database | Repeated favorite keeps one row | count=1 | count=1 | PASS |
| F-73 | Database | Repeated chat message keeps one row | count=1 | count=1 | PASS |
