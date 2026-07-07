A Python project has been created for this feature, with pytest configured.

Under `src/`, the project already contains a stub `travel_policy` module with a `Decision` enum and an unimplemented `evaluate_application` function.

We want to write the tests before implementing the decision logic.

Write table-driven pytest tests for `evaluate_application(...)`.

The rules are:
- Applications are approved when the applicant is under 70 and the trip is at most 90 days
- Applicants aged 70 or over are approved only if they have medical clearance
- Trips longer than 90 days go to manual review, whatever the applicant's age
- Applications for restricted destinations are declined regardless of anything else

Keep the stub unimplemented — the tests are expected to fail until the logic is written, but they must import and collect cleanly.
