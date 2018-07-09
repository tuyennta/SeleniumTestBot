package com.tuyennta.automation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new Scene(new WebViewPane("http://test.fscexpress.com")));
        stage.setFullScreen(false);
        stage.show();
	}

}