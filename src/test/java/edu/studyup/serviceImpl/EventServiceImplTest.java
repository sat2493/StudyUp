package edu.studyup.serviceImpl;

import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;

class EventServiceImplTest {

	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		// Create Student
		Student student1 = new Student();
		student1.setFirstName("John");
		student1.setLastName("Doe");
		student1.setEmail("JohnDoe@email.com");
		student1.setId(1);

		// Create Event2
		Event event2 = new Event();
		event2.setEventID(2);
		Calendar cal2 = Calendar.getInstance();
		cal2.set(Calendar.YEAR, 2025);
		cal2.set(Calendar.MONTH, Calendar.JANUARY);
		cal2.set(Calendar.DAY_OF_MONTH, 1);
		event2.setDate(cal2.getTime());
		event2.setName("Event 2 - in the future");
		Location location2 = new Location(-122, 37);
		event2.setLocation(location2);
		List<Student> eventStudents2 = new ArrayList<>();
		eventStudents2.add(student1);
		event2.setStudents(eventStudents2);
		DataStorage.eventData.put(event2.getEventID(), event2);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	@Test // ACTUAL BUG 1: Suppose to work for name length =20 but it fails
	void BUG_1_testupdateEventName_NameLength20_goodCase() throws StudyUpException {
		int eventID = 2;
		assertDoesNotThrow(() -> eventServiceImpl.updateEventName(eventID, "12345678912345678912"));
	}

	@Test // ACTUAL BUG 2: Missing if condition to eliminate out past events in the
			// function
	void BUG_2_testgetActiveEvents_ShouldExcludePastEvent1_goodCase() {
		// Create Student
		Student student7 = new Student();
		student7.setFirstName("Nhan");
		student7.setLastName("Nguyen");
		student7.setEmail("nhannguyen@email.com");
		student7.setId(7);
		// Create Event1 (PAST)
		Event event1 = new Event();
		event1.setEventID(1);
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2010);
		cal1.set(Calendar.MONTH, Calendar.JANUARY);
		cal1.set(Calendar.DAY_OF_MONTH, 1);
		event1.setDate(cal1.getTime());
		event1.setName("Event 1 - already pasted");
		Location location1 = new Location(-122, 37);
		event1.setLocation(location1);
		List<Student> eventStudents1 = new ArrayList<>();
		eventStudents1.add(student7);
		event1.setStudents(eventStudents1);
		DataStorage.eventData.put(event1.getEventID(), event1);

		List<Event> active_event_list = eventServiceImpl.getActiveEvents();
		assertEquals(1, active_event_list.size()); // This fails because it did not exclude the PAST event 1 that we
													// just created.
	}

	@Test // ACTUAL BUG 3: missing conditions to ensure that there are at most 2 students
			// for each event.
	void BUG_3_testaddStudentToEvent_ShouldRejectAddingThirdStudent_badCase() throws Exception {
		// Create Event1 (PAST) - to utilize function getPastEvents since we know it is
		// 100% bug free (told by prompt).
		Event event1 = new Event();
		event1.setEventID(1);
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2010);
		cal1.set(Calendar.MONTH, Calendar.JANUARY);
		cal1.set(Calendar.DAY_OF_MONTH, 1);
		event1.setDate(cal1.getTime());
		event1.setName("Event 1 - already pasted");
		Location location1 = new Location(-122, 37);
		event1.setLocation(location1);
		DataStorage.eventData.put(event1.getEventID(), event1);

		// Create Student 1
		Student student7 = new Student();
		student7.setFirstName("Nhan");
		student7.setLastName("Nguyen");
		student7.setEmail("nhannguyen@email.com");
		student7.setId(7);

		// Create Student 2
		Student student8 = new Student();
		student8.setFirstName("Simon");
		student8.setLastName("Tabligan");
		student8.setEmail("simontabligan@email.com");
		student8.setId(8);

		// Create Student 3
		Student student9 = new Student();
		student9.setFirstName("Xuan");
		student9.setLastName("Fei");
		student9.setEmail("xuanfei@email.com");
		student9.setId(9);

		eventServiceImpl.addStudentToEvent(student7, 1); // Add student ID 7 to event ID 1
		assertEquals(1, eventServiceImpl.getPastEvents().get(0).getStudents().size()); // It is okay to have 1 student
																						// by her/himself
																						// on an event.

		eventServiceImpl.addStudentToEvent(student8, 1); // Add student ID 8 to event ID 1
		assertEquals(2, eventServiceImpl.getPastEvents().get(0).getStudents().size()); // It is okay to have 2 students
																						// on an event.

		eventServiceImpl.addStudentToEvent(student9, 1); // Add student ID 9 to event ID 1
		assertEquals(2, eventServiceImpl.getPastEvents().get(0).getStudents().size()); // It is NOT okay to have 2
																						// students on an event.
																						// Therefore, we should NOT be
																						// able to add student9
																						// to event ID 1, leading to its
																						// # student count to
																						// be remaining at 2. However,
																						// in this case our method
																						// fails to do so.
	}

	// Test cases for "updateEventName" method.
	@Test // Suppose to throw exception for accessing an event that does NOT exist.
	void testupdateEventName_InvalidEventID_badCase1() {
		int eventID = 50;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 50");
		});
	}

	@Test // Suppose to throw exception for event name >20
	void testupdateEventName_NameLength27_badCase2() {
		int eventID = 2;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "123456789123456789123456789");
		});
	}

	@Test // This is a good test case, since the new name is only of length 11.
	void testupdateEventName_NameLength11_goodCase() throws StudyUpException {
		int eventID = 2;
		eventServiceImpl.updateEventName(eventID, "NEW Event 2");
		assertEquals("NEW Event 2", DataStorage.eventData.get(eventID).getName());
	}

	@Test // Test getActiveEvents with inputs that we expect the program will give out
			// valid output (event list size 1,
			// where event1 is the only event in that list.
	void testgetActiveEvents_ShouldReturnEventsListSizeOf1_goodCase() {
		List<Event> active_event_list = eventServiceImpl.getActiveEvents();
		assertEquals(1, active_event_list.size());
	}

	// Test cases for "getPastEvents" method. - This method is guaranteed by the
	// prompt to have no bug.
	// Test getPastEvents with inputs that we expect the program will give out valid
	// output (event list size 1,
	// where event2 is the only event in that list.
	@Test // COVERAGE purpose - test on getPastEvents
	void testgetPastEvents_ShouldReturnEventsListOfSize1_goodCase() {
		// Create a student
		Student student7 = new Student();
		student7.setFirstName("Nhan");
		student7.setLastName("Nguyen");
		student7.setEmail("nhannguyen@email.com");
		student7.setId(7);

		// Create Event1 (already in the past)
		Event event1 = new Event();
		event1.setEventID(1);
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2010); // This event 1 happened in the past.
		cal1.set(Calendar.MONTH, Calendar.JANUARY);
		cal1.set(Calendar.DAY_OF_MONTH, 1);
		event1.setDate(cal1.getTime());
		event1.setName("Event 1 - already pasted");
		Location location1 = new Location(-122, 37);
		event1.setLocation(location1);
		List<Student> eventStudents1 = new ArrayList<>();
		eventStudents1.add(student7);
		event1.setStudents(eventStudents1);
		DataStorage.eventData.put(event1.getEventID(), event1);
		List<Event> past_event_list = eventServiceImpl.getPastEvents();
		assertEquals(1, past_event_list.size());
	}

	// Test cases for "addStudentToEvent" method.
	@Test // Test addStudentToEvent goodCase1 - add student to an event in which no
			// student has been on it yet.
			// this is for edge COVERAGE purpose
	void testaddStudentToEvent_ShouldSucessfullyAddAnotherStudent_goodCase1() throws Exception {
		// Create Event1 (PAST) - to utilize function getPastEvents since we know it is
		// 100% bug free (told by prompt).
		Event event1 = new Event();
		event1.setEventID(1);
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2010); // The event 1 happened in the past.
		cal1.set(Calendar.MONTH, Calendar.JANUARY);
		cal1.set(Calendar.DAY_OF_MONTH, 1);
		event1.setDate(cal1.getTime());
		event1.setName("Event 1 - already pasted");
		Location location1 = new Location(-122, 37);
		event1.setLocation(location1);
		DataStorage.eventData.put(event1.getEventID(), event1);

		// Create a student
		Student student7 = new Student();
		student7.setFirstName("Nhan");
		student7.setLastName("Nguyen");
		student7.setEmail("nhannguyen@email.com");
		student7.setId(7);
		eventServiceImpl.addStudentToEvent(student7, 1); // Add student ID 1 to event ID 1

		// Confirm that the student with ID 7 was added successfully added to event ID 1
		// by the method "addStudentToEvent".
		List<Student> event_2_student_list = eventServiceImpl.getPastEvents().get(0).getStudents();
		Student student_just_added = event_2_student_list.get(0);
		assertEquals("Nhan", student_just_added.getFirstName());
		assertEquals("Nguyen", student_just_added.getLastName());
		assertEquals("nhannguyen@email.com", student_just_added.getEmail());
		assertEquals(7, student_just_added.getId());
	}

	@Test // Test addStudentToEvent goodCase2 - add student to an event in which 1 other
			// student has already been on the event
	// this is for edge coverage purpose
	void testaddStudentToEvent_ShouldSucessfullyAddAnotherStudent_goodCase2() throws Exception {
		// Create Event1 (PAST) - to utilize function getPastEvents since we know it is
		// 100% bug free (told by prompt).
		Event event1 = new Event();
		event1.setEventID(1);
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2010); // This event already happened in the past.
		cal1.set(Calendar.MONTH, Calendar.JANUARY);
		cal1.set(Calendar.DAY_OF_MONTH, 1);
		event1.setDate(cal1.getTime());
		event1.setName("Event 1 - already pasted");
		Location location1 = new Location(-122, 37);
		event1.setLocation(location1);
		DataStorage.eventData.put(event1.getEventID(), event1);

		// Create Student 1
		Student student7 = new Student();
		student7.setFirstName("Nhan");
		student7.setLastName("Nguyen");
		student7.setEmail("nhannguyen@email.com");
		student7.setId(7);
		eventServiceImpl.addStudentToEvent(student7, 1); // Add student ID 7 to event ID 1

		// Create Student 2
		Student student8 = new Student();
		student8.setFirstName("Simon");
		student8.setLastName("Tabligan");
		student8.setEmail("simontabligan@email.com");
		student8.setId(8);
		eventServiceImpl.addStudentToEvent(student8, 1); // Add student ID 8 to event ID 1

		// Confirm that the students are added onto the event successfully
		List<Student> event_2_student_list = eventServiceImpl.getPastEvents().get(0).getStudents();
		assertEquals(2, event_2_student_list.size());

		Student student_just_added_1 = event_2_student_list.get(0);
		assertEquals("Nhan", student_just_added_1.getFirstName());
		assertEquals("Nguyen", student_just_added_1.getLastName());
		assertEquals("nhannguyen@email.com", student_just_added_1.getEmail());
		assertEquals(7, student_just_added_1.getId());

		Student student_just_added_2 = event_2_student_list.get(1);
		assertEquals("Simon", student_just_added_2.getFirstName());
		assertEquals("Tabligan", student_just_added_2.getLastName());
		assertEquals("simontabligan@email.com", student_just_added_2.getEmail());
		assertEquals(8, student_just_added_2.getId());
	}

	@Test // Test addStudentToEvent goodCase3 - this is to test that the function gives
			// out exception when the event does NOT exist
	void testaddStudentToEvent_InvalidEventID_badCase1() throws Exception {
		// Create a student
		Student student7 = new Student();
		student7.setFirstName("Nhan");
		student7.setLastName("Nguyen");
		student7.setEmail("nhannguyen@email.com");
		student7.setId(7);
		// Add student ID 7 to event ID 100 where event ID 100 does NOT exist should
		// throw an exception.
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student7, 100);
		});
	}

	// Test cases for "deleteEvent" method. - This method is guaranteed by the
	// prompt to have no bug.
	@Test // COVERAGE purpose - test on deleteEvent
	void testdeleteEvent_ShouldSucessfullyDeleteEvent_goodCase() {
		int eventID = 2;
		eventServiceImpl.deleteEvent(eventID);
		assertEquals(null, eventServiceImpl.deleteEvent(eventID)); // If the event is already deleted and you try to
																	// delete it again,
																	// it should return null.
	}

}
