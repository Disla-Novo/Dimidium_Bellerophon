# Future Investigations

This document holds speculative ideas that may be worth exploring but are
outside the core Gravity Hub printer-management roadmap. Listing an idea here
does not mean it is approved, committed, or scheduled.

## Predictive model for print-failure risk

Investigate whether a trained or otherwise empirically evaluated model can
analyze generated G-code and estimate the likelihood of a print failure, such
as spaghetti. The goal is risk prediction from evidence.

 G-code alone may not contain enough information to predict every failure mode, so assess
whether machine profiles, telemetry, camera data, or labeled print outcomes
are needed.

Before considering a user-facing prediction, investigate:

- Which failure modes the model is intended to predict and which inputs are
  available and useful.
- Whether representative, consented datasets can be collected and how privacy,
  retention, and data sharing would be handled.
- What baseline deterministic checks provide, so model performance can be
  compared against a meaningful non-model approach.
- How to measure false positives, false negatives, calibration, and usefulness
  across different machines, firmware, materials, and failure modes.
- How to communicate the estimated risk and uncertainty without implying that
  a prediction guarantees success or failure.

Any future capability should begin as advisory research, not as a control
system that starts, stops, or changes a print automatically. This research is
not approved or scheduled product work and should not block core connectivity,
file transfer, or job-management milestones.
