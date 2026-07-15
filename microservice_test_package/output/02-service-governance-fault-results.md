# Test Results

- Total: 13
- Passed: 13
- Failed: 0

| ID | Category | Test | Expected | Actual | Result |
|---|---|---|---|---|---|
| GS-01 | Fault injection | work-service becomes unavailable | Unavailable | True | PASS |
| GS-02 | Fault isolation | Comment creation is blocked while work-service is down | Non-200 business result | HTTP=200, code=500 | PASS |
| GS-03 | Fault isolation | Favorite creation is blocked while work-service is down | Non-200 business result | HTTP=200, code=400 | PASS |
| GS-04 | Recovery | work-service is rediscovered after restart | Available | True | PASS |
| GS-05 | Recovery | Comment cross-service validation works after recovery | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| GS-10 | Fault injection | auth-service becomes unavailable | Unavailable | True | PASS |
| GS-11 | Fallback | Broadcast fails in a controlled way while auth-service is down | Non-200 business result | HTTP=200, code=500 | PASS |
| GS-12 | Recovery | auth-service is rediscovered after restart | Available | True | PASS |
| GS-13 | Recovery | Broadcast succeeds after auth-service recovery | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| GS-20 | Fault injection | notification-service becomes unavailable | Unavailable | True | PASS |
| GS-21 | Degradation | Chat main flow succeeds while notification-service is down | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
| GS-22 | Recovery | notification-service is rediscovered after restart | Available | True | PASS |
| GS-23 | Recovery | Chat flow succeeds after notification-service recovery | ApiResponse.code=200 | HTTP=200, code=200 | PASS |
