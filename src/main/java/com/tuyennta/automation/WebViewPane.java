package com.tuyennta.automation;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tuyennta.automation.bot.AutomateBot;
import com.tuyennta.automation.dto.TestScript;
import com.tuyennta.automation.dto.TestStep;
import com.tuyennta.automation.record.DOMEventListener;
import com.tuyennta.automation.utils.Const;
import com.tuyennta.automation.utils.Timer;

import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class WebViewPane extends BorderPane {
	HBox box = new HBox();
	TextField urlBox = new TextField();
	Button recordButton = new Button(Const.LOADING_TEXT);
	Button goButton = new Button();
	Button captureButton = new Button("Capture");
	WebView browser = new WebView();
	WebEngine engine = browser.getEngine();
	File localStorage = new File("LocalStorage");
	

	public WebViewPane(String initURL) {
		if (!localStorage.exists()) {
			localStorage.mkdir();
		}
		engine.setUserDataDirectory(localStorage);
		
		initLayout();
		loadWeb(initURL);
		addJavaFXEventListener();
		addDOMEventListener();
	}
	
	private void loadWeb (String url) {
		
		
		
		//page load
		engine.load(url);
		
		//add test step
		TestStep step = new TestStep();
		step.setAction(Const.ACTION_OPEN_BROWSER);
		step.setXpath("ff");
		step.setParameters(url);
		TestScript.getInstance().addStep(step);
	}

	private void addJavaFXEventListener() {

		captureButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				TestStep step = new TestStep();
				step.setAction(Const.ACTION_CAPTURE_SCREEN);
				TestScript.getInstance().addStep(step);
			}
		});
		
		recordButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				AutomateBot.generateScript();
			}
		});
		
		goButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				String url = urlBox.getText();
				loadWeb(url);
			}
		});
		
		goButton.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					String url = urlBox.getText();
					loadWeb(url);
				}
			}
		});

		urlBox.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					String url = urlBox.getText();
					loadWeb(url);
				}
			}
		});
		
		urlBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				urlBox.selectAll();
			}
		});
	}

	private void addDOMEventListener() {
		engine.getLoadWorker().stateProperty().addListener((obs, oldState, currentState) -> {
			recordButton.setText(Const.LOADING_TEXT);
			if (currentState == State.SUCCEEDED) {

				//start wait timer
				Timer.getInstance().setInterval(System.currentTimeMillis());
				
				JSObject window = (JSObject) engine.executeScript("window");
				window.setMember("listener", new DOMEventListener(engine, recordButton));

				waitAjaxLoadDone(engine);

				urlBox.setText(engine.getLocation());
			}
		});
		engine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:60.0) Gecko/20100101 Firefox/60.0");
	}

	private void waitAjaxLoadDone(WebEngine webEngine) {
		String script = "var intervalId = setInterval(function(){\r\n"
					+ "    if (Object.keys(require.s.contexts._.registry).length === 0){\r\n"
					+ "		listener.addListeners();\r\n" 
					+ "		clearInterval(intervalId);\r\n" + "	}\r\n" + "}, 500);";
		script = "listener.addListeners();";
		webEngine.executeScript(script);
	}

	private void initLayout() {
		// size of app
		setPrefSize(1024, 568);
		// width of box
		box.setPrefWidth(getPrefWidth());
		box.getChildren().addAll(recordButton, captureButton, urlBox, goButton);
		// width of recording label
		recordButton.setPrefWidth(box.getPrefWidth() * 0.1);
		recordButton.setAlignment(Pos.CENTER);
		//capture button
		captureButton.setPrefWidth(box.getPrefWidth() * 0.1);
		captureButton.setAlignment(Pos.CENTER);
		// width of url box
		urlBox.setPrefWidth(box.getPrefWidth() * 0.7);
		urlBox.setText(engine.getLocation());
		// width of go box
		goButton.setPrefWidth(box.getPrefWidth() * 0.1);
		goButton.setText("Go");
		// add box to top of app
		setTop(box);
		// add webview below
		setCenter(browser);

	}
}
