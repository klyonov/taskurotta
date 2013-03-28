package ru.taskurotta.dropwizard.client.serialization.wrapper;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.taskurotta.dropwizard.client.serialization.ResultContainerDeserializer;
import ru.taskurotta.server.transport.DecisionContainer;

public class DecisionContainerWrapper {

	private DecisionContainer decisionContainer;

	public DecisionContainerWrapper() {
	}

	public DecisionContainerWrapper(DecisionContainer decisionContainer) {
		this.decisionContainer = decisionContainer;
	}

	public DecisionContainer getResultContainer() {
		return decisionContainer;
	}

	@JsonDeserialize(using = ResultContainerDeserializer.class)
	public void setResultContainer(DecisionContainer decisionContainer) {
		this.decisionContainer = decisionContainer;
	}

	@Override
	public String toString() {
		return "ResultContainerWrapper [resultContainer=" + decisionContainer + "]";
	}

}
