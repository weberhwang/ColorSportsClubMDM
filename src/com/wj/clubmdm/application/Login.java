/* 
 * Colors Sports Club MDM 登入頁面
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;
	
import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class Login extends Application {
	private Logger logger = Logger.getLogger(Login.class);
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene = new Scene(root,1200,700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("COLOR SPORTS CLUB MDM_V1.0");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false); //不可調整視窗大小
			primaryStage.show();
			logger.info("TEST");
		} catch(Exception e) {
			logger.info(e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
