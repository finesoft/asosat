package org.corant.asosat.ddd.service;

import javax.ws.rs.Priorities;

import org.corant.asosat.ddd.gateway.JsonContextResolver;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderListener;

/**
 *  临时调整做法
 * @author bingo
 *
 */
public class MpRestClientBuilderListener implements RestClientBuilderListener{

	@Override
	public void onNewBuilder(RestClientBuilder builder) {
		builder.register(JsonContextResolver.class, Priorities.USER-1);
	}

}
