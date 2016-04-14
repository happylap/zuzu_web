/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.lap.zuzuweb;

import org.apache.commons.lang3.StringUtils;

/**
 * This class captures all of the configuration settings. These environment
 * properties are defined in the BeanStalk container configuration tab.
 */
public class Configuration {

	/**
	 * 
	 */
	public static final String AWS_ACCESS_KEY_ID = StringUtils.defaultIfEmpty(System.getProperty("AWS_ACCESS_KEY_ID"),
			Secrets.AWS_ACCESS_KEY_ID);

	/**
	 * 
	 */
	public static final String AWS_SECRET_ACCESS_KEY = StringUtils
			.defaultIfEmpty(System.getProperty("AWS_SECRET_ACCESS_KEY"), Secrets.AWS_SECRET_ACCESS_KEY);

	/**
	 * The identity pool to use
	 */
	public static final String IDENTITY_POOL_ID = StringUtils.defaultIfEmpty(System.getProperty("IDENTITY_POOL_ID"),
			Secrets.AWS_COGNITO_IDENTITY_POOL_ID);

	/**
	 * The developer provider name to use
	 */
	public static final String DEVELOPER_PROVIDER_NAME = StringUtils
			.defaultIfEmpty(System.getProperty("DEVELOPER_PROVIDER_NAME"), Secrets.AWS_COGNITO_DEVELOPER_PROVIDER_NAME);

	/**
	 * The region to run against
	 */
	public static final String REGION = StringUtils.defaultIfEmpty(System.getProperty("REGION"),
			Secrets.AWS_COGNITO_REGION);

	/**
	 * The duration for which the open id token will be valid
	 */
	public static final String SESSION_DURATION = "900";

}
