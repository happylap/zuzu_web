package com.lap.zuzuweb.handler;

import java.util.Map;

import com.lap.zuzuweb.handler.payload.Validable;

public interface RequestHandler<V extends Validable> {

    Answer process(V value, Map<String, String> urlParams);

}
