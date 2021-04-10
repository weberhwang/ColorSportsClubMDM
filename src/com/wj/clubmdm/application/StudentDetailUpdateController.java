package com.wj.clubmdm.application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.wj.clubmdm.vo.RollCallDetail;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class StudentDetailUpdateController extends Application{
	
	private Logger logger = Logger.getLogger(StudentDetailUpdateController.class);
	
	@FXML
    private ImageView imgvMember;

    @FXML
    private ImageView imgvQrCode;

    @FXML
    private Button btnImgUpdate;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfSchool;

    @FXML
    private TextField tfBirthday;

    @FXML
    private TextField tfSchoolDesc;

    @FXML
    private TextField tfSex;

    @FXML
    private TextField tfHeight;

    @FXML
    private TextField tfStudentNo;

    @FXML
    private TextField tfAddress;

    @FXML
    private TextField tfGuardian2;

    @FXML
    private TextField tfContectTel2;

    @FXML
    private TextField tfContectTel1;

    @FXML
    private TextField tfGuardian1;

    @FXML
    private TextField tfMark;

    @FXML
    private TextField tfId;

    @FXML
    private CheckBox ckbColorClub;

    @FXML
    private CheckBox ckbSchoolClub;

    @FXML
    private CheckBox ckbWinterSummerCamp;

    @FXML
    private TextField tfResultInfo;

    @FXML
    private DatePicker dpLeaveDate;

    @FXML
    private ChoiceBox<String> cbLevel;

    @FXML
    private ChoiceBox<String> cbCourseKind;

    @FXML
    private ChoiceBox<String> cbDepartment;

    @FXML
    private ChoiceBox<String> cbSpecial;

    @FXML
    private ChoiceBox<String> cbTranfer;

    @FXML
    private DatePicker dpJoinDate;

    @FXML
    private ChoiceBox<String> cbStatus;
    
	public void initialize() {
	
		ObservableList<String> sexItems = FXCollections.observableArrayList(null,"男", "女");	
		ObservableList<String> schoolDescItems = FXCollections.observableArrayList
				(null,
				 "學齡",
				 "幼小",
				 "幼中",
				 "幼大",
				 "小一",
				 "小二",
				 "小三",
				 "小四",
				 "小五",
				 "小六",
				 "國一",
				 "國二",
				 "國三",
				 "高一",
				 "高二",
				 "高三",
				 "大一",
				 "大二",
				 "大三",
				 "大四",
				 "成人",
				 "其它");	
		ObservableList<String> yesnoItems = FXCollections.observableArrayList("是", "否");	
		ObservableList<String> departmentItems = FXCollections.observableArrayList(null,"陽光", "仁愛", "隨選");	
		ObservableList<String> courseKindItems = FXCollections.observableArrayList(null,"競速", "花式", "雙棲");	
		ObservableList<String> levelItems = FXCollections.observableArrayList(null,"初級", "中級", "高級", "C組", "B組", "A組");	
		ObservableList<String> StatusItems = FXCollections.observableArrayList(null,"停課","正式","退遂");
		cbLevel.autosize();
		cbLevel.setItems(levelItems);
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		
	}

}
