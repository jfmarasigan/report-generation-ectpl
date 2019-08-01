package com.cpi.ectpl.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.cpi.ectpl.report.entities.FunctionParameters;

public class LambdaFunctionHandler implements RequestHandler<FunctionParameters, String> {

	@Override
	public String handleRequest(FunctionParameters input, Context context) {
		if (input.getIsPing() || input.getIsPing() != null) {
			System.out.println("Ping event");
		}
		
		System.out.println("Generating reports using input : " + input);
		ProcessHandler handler = new ProcessHandler(input);
		return handler.process();
	}
}
