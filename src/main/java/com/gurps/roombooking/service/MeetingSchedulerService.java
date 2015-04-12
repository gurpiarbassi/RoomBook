package com.gurps.roombooking.service;

/**
 * Meeting scheduler service
 * This service has only one objective and that is to process the schedule.
 * Implementations of this service can process the schedule how they like.
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public interface MeetingSchedulerService {

    void produceSchedule();

}
