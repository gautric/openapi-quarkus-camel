/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:getDetails").id("getDetails")
        	.to("atlasmap:map/request.adm")
        	.to("direct:call-backend")
        	.to("log:org.apache.camel?showAll=true&multiline=true&level=ERROR")
        	.to("atlasmap:map/response.adm");

        from("direct:call-backend").id("call-backend")
        	.removeHeaders("*")
        	.setHeader(Exchange.HTTP_METHOD, constant("POST"))
        	.setHeader(Exchange.CONTENT_TYPE, constant("application/xml"))
        	.to("direct:circuitbreaker");
        
        from("direct:circuitbreaker").id("circuitbreaker")
	        .circuitBreaker()
	        	.to("http:{{api.gateway.host}}/camel/individual/details")
	        .onFallback()
	            .to("language:constant:resource:classpath:data/individual-failback.xml")
	        .end();
    }
}
