package com.eschool.beans;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="attendance")
public class Attendance {
	@Id
	@GeneratedValue
    private int attendanceId;
    private int eventId;
    private int userId;
    private String hallNo;
    private boolean present;
	public Attendance(int eventId, int userId, String hallNo,boolean present) {
		this.eventId = eventId;
		this.userId = userId;
		this.hallNo = hallNo;
		this.present=present;
	}
	public boolean getPresent() {
		return present;
	}
	public void setPresent(boolean present) {
		this.present = present;
	}
	public String getHallNo() {
		return hallNo;
	}
	public void setHallNo(String hallNo) {
		this.hallNo = hallNo;
	}
	public Attendance() {		
	}
	public int getAttendanceId() {
		return attendanceId;
	}
	public void setAttendanceId(int attendanceId) {
		this.attendanceId = attendanceId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
