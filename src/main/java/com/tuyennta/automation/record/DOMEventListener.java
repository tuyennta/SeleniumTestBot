package com.tuyennta.automation.record;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.sun.webkit.dom.KeyboardEventImpl;
import com.tuyennta.automation.dto.TestScript;
import com.tuyennta.automation.dto.TestStep;
import com.tuyennta.automation.utils.Const;
import com.tuyennta.automation.utils.Timer;
import com.tuyennta.automation.utils.XPathHelper;

import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;

public class DOMEventListener {

	WebEngine webEngine;
	Button recording;
	XPathHelper xpathHelper;
	protected String currentXpath;

	public DOMEventListener(WebEngine webEngine, Button recordButton) {
		this.webEngine = webEngine;
		this.recording = recordButton;
	}

	/**
	 * Register event listener for web elements This function will be called from
	 * JavaScript
	 */
	public void addListeners() {
		Document doc = webEngine.getDocument();
		xpathHelper = XPathHelper.getInstance(webEngine);

		// add event for <input/> elements
		NodeList input = doc.getElementsByTagName("input");
		System.out.println("Num inputs: " + input.getLength());
		for (int i = 0; i < input.getLength(); i++) {
			((EventTarget) input.item(i)).addEventListener("focus", focusListener, false);
			((EventTarget) input.item(i)).addEventListener("blur", blurListener, false);
			if ("file".equalsIgnoreCase(((Element) input.item(i)).getAttribute("type"))
					|| "radio".equalsIgnoreCase(((Element) input.item(i)).getAttribute("type"))
					|| "checkbox".equalsIgnoreCase(((Element) input.item(i)).getAttribute("type"))) {
				((EventTarget) input.item(i)).addEventListener("click", click, false);
			}
		}

		// add event for <textarea/> elements
		NodeList textarea = doc.getElementsByTagName("textarea");
		System.out.println("Num textareas: " + textarea.getLength());
		for (int i = 0; i < textarea.getLength(); i++) {
			((EventTarget) textarea.item(i)).addEventListener("focus", focusListener, false);
			((EventTarget) textarea.item(i)).addEventListener("blur", blurListener, false);
		}

		// add event for <a/> elements
		NodeList hyperlink = doc.getElementsByTagName("a");
		System.out.println("Num links: " + hyperlink.getLength());
		for (int i = 0; i < hyperlink.getLength(); i++) {
			((EventTarget) hyperlink.item(i)).addEventListener("click", click, false);

		}

		// add event for <select/> elements
		NodeList select = doc.getElementsByTagName("select");
		System.out.println("Num selects: " + select.getLength());
		for (int i = 0; i < select.getLength(); i++) {
			((EventTarget) select.item(i)).addEventListener("blur", blurListener, false);
		}

		// add event for <button/> elements
		NodeList button = doc.getElementsByTagName("button");
		System.out.println("Num buttons: " + button.getLength());
		for (int i = 0; i < button.getLength(); i++) {
			((EventTarget) button.item(i)).addEventListener("click", click, false);

		}

		NodeList form = doc.getElementsByTagName("form");
		for (int i = 0; i < form.getLength(); i++) {
			// ((EventTarget) form.item(i)).addEventListener("submit", submitListener,
			// false);
			((EventTarget) form.item(i)).addEventListener("keypress", keypressListener, false);
		}

		// all events registered
		// change status to recording
		recording.setText(Const.RECORDING_TEXT);

		// because of javascript page load will not be handled by Selenium, so that we
		// will add a wait step here
		// add wait timer to script
		TestStep step = new TestStep();
		step.setAction("wait");
		step.setParameters(String.valueOf(System.currentTimeMillis() - Timer.getInstance().getInterval() + 50));
		TestScript.getInstance().addStep(step);
	}

	private EventListener keypressListener = new EventListener() {

		@Override
		public void handleEvent(Event evt) {
			// if press ENTER key
			if (((KeyboardEventImpl) evt).getKeyCode() == 13) {
				isSubmitFired = true;
			}

		}
	};

	private EventListener focusListener = new EventListener() {

		@Override
		public void handleEvent(Event evt) {
			currentXpath = xpathHelper.getXpath(((Element) evt.getTarget()));
		}
	};

	private EventListener blurListener = new EventListener() {

		@Override
		public void handleEvent(Event evt) {
			TestStep testStep = new TestStep();
			String xpath = xpathHelper.getXpath(((Element) evt.getTarget()));
			testStep.setXpath(xpath);

			String nodeName = ((Element) evt.getTarget()).getNodeName();
			if (nodeName.equalsIgnoreCase("select")) {
				testStep.setAction(Const.ACTION_SELECT_BY_VALUE);
				testStep.setParameters(xpathHelper.getSelectionValueByXpath(xpath));
			} else if (nodeName.equalsIgnoreCase("input")) {
				String inputText = xpathHelper.getElementValueByXpath(xpath);

				// if this input element is special input using bootstrap-tagsinput
				if (inputText.isEmpty()) {
					Node parent = ((Element) evt.getTarget()).getParentNode();
					if (((Element) parent).getAttribute("class").contains("bootstrap-tagsinput")) {
						Node hidenInput = parent.getNextSibling();
						if (hidenInput.getNodeName().equalsIgnoreCase("input")
								&& ((Element) hidenInput).getAttribute("style").contains("display: none")) {
							String hidenXpath = xpathHelper.getXpath(((Element) hidenInput));
							inputText = xpathHelper.getElementValueByXpath(hidenXpath);
						}

					}
				}
				testStep.setAction(Const.ACTION_SET_TEXT);
				testStep.setParameters(inputText);
			} else if (nodeName.equalsIgnoreCase("textarea")) {
				testStep.setAction(Const.ACTION_SET_TEXT);
				testStep.setParameters(xpathHelper.getElementValueByXpath(xpath));
			}

			TestScript.getInstance().addStep(testStep);
		}
	};

	private EventListener click = new EventListener() {

		@Override
		public void handleEvent(Event evt) {

			if (isSubmitFired) {
				TestStep testStep = new TestStep();
				testStep.setAction(Const.ACTION_SET_TEXT);
				testStep.setXpath(currentXpath);
				testStep.setParameters(xpathHelper.getElementValueByXpath(currentXpath));
				TestScript.getInstance().addStep(testStep);
				isSubmitFired = false;
			}

			TestStep testStep = new TestStep();
			String xpath = xpathHelper.getXpath(((Element) evt.getTarget()));
			testStep.setAction(Const.ACTION_CLICK);
			testStep.setXpath(xpath);
			TestScript.getInstance().addStep(testStep);
		}
	};

	boolean isSubmitFired = false;
	// private EventListener submitListener = new EventListener() {
	//
	// @Override
	// public void handleEvent(Event evt) {
	// isSubmitFired = true;
	// }
	// };

}
