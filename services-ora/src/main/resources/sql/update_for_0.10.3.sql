alter table TSK_SCHEDULED add LIMIT number;
update TSK_SCHEDULED s1 set limit = (select queue_limit from TSK_SCHEDULED s2 where s1.ID = s2.ID);
alter table tsk_scheduled drop column queue_limit;

ALTER TABLE TSK_INTERRUPTED_TASKS ADD TYPE NUMBER;
COMMENT ON COLUMN TSK_INTERRUPTED_TASKS.TYPE IS 'Task type';
CREATE INDEX TSK_INTERRUPTED_TASKS_TYPE ON TSK_INTERRUPTED_TASKS(TYPE);