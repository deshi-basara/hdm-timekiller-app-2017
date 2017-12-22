package de.hdm.dp.bd.chronophage.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TaskTest {
    private Task task;

    @Before
    public void setUp() {
        task = new Task(0, "Test");
    }

    @Test
    public void start_newTask_executes() {
        task.start();
    }

    @Test(expected = Task.TaskException.class)
    public void start_taskAlreadyStarted_throwsException() {
        task.start();
        task.start();
    }

    @Test
    public void stop_taskAlreadyStarted_executes() {
        task.start();
        task.stop();
    }

    @Test(expected = Task.TaskException.class)
    public void stop_taskNotYetStarted_throwsException() {
        task.stop();
    }

    @Test
    public void getOverallDuration_newTask_returnsZero() {
        assertEquals(0, task.getOverallDuration());
    }

    @Test
    public void getOverallDuration_taskHasRecord_returnsMoreThanZero() throws Exception {
        task.start();
        Thread.sleep(100);
        task.stop();
        Assert.assertTrue(0 < task.getOverallDuration());
    }

    public void getWithRecordsBefore_noRecordsBefore_overallDurationIsZero() throws Exception {
        //setup: get date before recording once
        final Date now = Date.from(Instant.now());
        Thread.sleep(100);
        task.start();
        Thread.sleep(100);
        task.stop();
        // test
        Task before = task.getWithRecordsBefore(now);
        assertEquals(0, before.getOverallDuration());
    }

    public void getWithRecordsBefore_onlyRecordsBefore_overallDurationIsUnchanged() throws Exception {
        //setup: record once before getting date
        task.start();
        Thread.sleep(100);
        task.stop();
        Thread.sleep(100);
        final Date now = Date.from(Instant.now());
        //test
        final long overallDurationWithoutFilter = task.getOverallDuration();
        Task before = task.getWithRecordsBefore(now);
        assertEquals(overallDurationWithoutFilter, before.getOverallDuration());
    }

    public void getWithRecordsBefore_oneRecodBeforeOneAfter_onlyDurationOfRecordBeforeReturned() throws Exception {
        //setup: record once before getting date
        task.start();
        Thread.sleep(100);
        task.stop();
        Thread.sleep(100);
        final Date now = Date.from(Instant.now());
        final long overallDurationWithOneRecord = task.getOverallDuration();
        task.start();
        Thread.sleep(100);
        task.stop();
        //ensure setup worked
        assertNotEquals(task.getOverallDuration(), overallDurationWithOneRecord);
        //test
        Task before = task.getWithRecordsBefore(now);
        assertEquals(overallDurationWithOneRecord, before.getOverallDuration());
    }

    public void getWithRecordsAfter_noRecordsAfter_overallDurationIsZero() throws Exception {
        //setup: get date after recording once
        task.start();
        Thread.sleep(100);
        task.stop();
        Thread.sleep(100);
        final Date now = Date.from(Instant.now());
        // test
        Task before = task.getWithRecordsAfter(now);
        assertEquals(0, before.getOverallDuration());
    }

    public void getWithRecordsAfter_onlyRecordsAfter_overallDurationIsUnchanged() throws Exception {
        //setup: record once before getting date
        final Date now = Date.from(Instant.now());
        Thread.sleep(100);
        task.start();
        Thread.sleep(100);
        task.stop();
        //test
        final long overallDurationWithoutFilter = task.getOverallDuration();
        Task before = task.getWithRecordsAfter(now);
        assertEquals(overallDurationWithoutFilter, before.getOverallDuration());
    }

    public void getWithRecordsAfter_oneRecodBeforeOneAfter_onlyDurationOfRecordAfterReturned() throws Exception {
        //setup: record once before getting date
        task.start();
        Thread.sleep(100);
        task.stop();
        Thread.sleep(100);
        final Date now = Date.from(Instant.now());
        final long overallDurationWithOneRecord = task.getOverallDuration();
        task.start();
        Thread.sleep(100);
        task.stop();
        final long durationOfRecordAfter = task.getOverallDuration() - overallDurationWithOneRecord;
        //ensure setup worked
        assertNotEquals(task.getOverallDuration(), durationOfRecordAfter);
        //test
        Task before = task.getWithRecordsAfter(now);
        assertEquals(durationOfRecordAfter, before.getOverallDuration());
    }
}
