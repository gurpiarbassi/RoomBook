package com.gurps.roombooking;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;

import com.gurps.roombooking.service.IMeetingSchedulerService;

public class MeetingSchedulerTest {

	@Test(expected=IOException.class)
	public void testInvalidPathsSupplied() throws IOException {
		MeetingScheduler.main(new String[]{"/home\0", "bar"});
	}
	
	@Test(expected=IOException.class)
	public void testInputFileNonExistent() throws IOException {
		MeetingScheduler.main(new String[]{"foo", "bar"});
	}
	
	@Test
	public void testHappyPath() throws IOException {
		final IMeetingSchedulerService meetingSchedulerService = mock(IMeetingSchedulerService.class);
		final MeetingScheduler meetingScheduler = new MeetingScheduler(meetingSchedulerService);
		meetingScheduler.run();
		
		verify(meetingSchedulerService).produceSchedule();
	}

}
