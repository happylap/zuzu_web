package com.lap.zuzuweb.handler;

import java.util.Map;

import com.lap.zuzuweb.handler.payload.Validable;

public interface RequestArrayHandler {

	Answer process(Validable[] values, Map<String, String> urlParams);
}
