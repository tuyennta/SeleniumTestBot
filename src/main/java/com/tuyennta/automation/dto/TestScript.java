package com.tuyennta.automation.dto;

import java.util.ArrayList;
import java.util.List;

public class TestScript {
	
	private TestScript() {
		listStep = new ArrayList<>();
	}
	
	private static TestScript INSTANCE;
	private List<TestStep> listStep;
	
	public static TestScript getInstance () {
		if (INSTANCE == null) {
			INSTANCE = new TestScript();
		}
		return INSTANCE;
	}

	public List<TestStep> getListStep() {
		return listStep;
	}
	
	public void removeAllSteps() {
		this.listStep.removeAll(listStep);
	}

	public void addStep(TestStep step) {
		this.listStep.add(step);
	}
}
