CREATE INDEX TSK_PROCESS_END_TIME_IDX ON TSK_PROCESS(END_TIME);
ALTER TABLE TSK_PROCESS ADD TASK_LIST VARCHAR2(500 char);