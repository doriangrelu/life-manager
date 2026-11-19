-- Rappels saisis par les membres (lot 6a) : visibles par tout le foyer.
-- anchor_on : date de départ de la récurrence (les dates suivantes en sont calculées).
-- recurrence : NONE, MONTHLY, QUARTERLY, YEARLY ; notice : SAME_DAY, ONE_WEEK, ONE_MONTH.
CREATE TABLE personal_reminder
(
    id           UUID PRIMARY KEY,
    household_id UUID         NOT NULL,
    title        VARCHAR(60)  NOT NULL,
    note         VARCHAR(280),
    due_on       DATE         NOT NULL,
    anchor_on    DATE         NOT NULL,
    recurrence   VARCHAR(10)  NOT NULL,
    notice       VARCHAR(10)  NOT NULL
);
CREATE INDEX personal_reminder_household ON personal_reminder (household_id, due_on);
