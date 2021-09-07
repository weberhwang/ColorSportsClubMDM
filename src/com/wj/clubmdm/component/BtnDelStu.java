package com.wj.clubmdm.component;

import javafx.scene.control.Button;

public class BtnDelStu extends Button{ 
	private String StudentNo;
	private String Id;
	private String Name;
	
	
	public String getStudentNo() {
		return StudentNo;
	}
	public void setStudentNo(String studentNo) {
		this.StudentNo = studentNo;
	}
	public String getID() {
		return Id;
	}
	public void setID(String id) {
		this.Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		this.Name = name;
	}

}
