/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.tail.source;

import java.io.File;
import java.nio.file.WatchEvent;

import org.neo4j.cypher.internal.compiler.v2_2.ast.hasAggregateButIsNotAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageProducers;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.file.FileInboundChannelAdapterSpec;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.config.FileTailInboundChannelAdapterFactoryBean;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.tail.ApacheCommonsFileTailingMessageProducer;
import org.springframework.integration.file.tail.FileTailingMessageProducerSupport;
import org.springframework.integration.file.tail.OSDelegatingFileTailingMessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.stereotype.Service;


//@ComponentScan
@EnableBinding(Source.class)
@EnableConfigurationProperties(TailSourceProperties.class)
public class TailSourceConfiguration {
	
    
	private final Logger logger = LoggerFactory.getLogger(TailSourceConfiguration.class);
	
	@Autowired
    private TailSourceProperties properties;
   
    @Autowired
    Source source;
    
    @Autowired
    FileTailingMessageProducerSupport tailer;

    @Autowired
    TailerRestartService tailerRestart;
    
//    @Bean
//    public PollableChannel fileInputChannel() {
//    	return new QueueChannel();    
//    }

    @Bean
    public DirectChannel fileInputChannel() {
    	return new DirectChannel();    
    }
    
    @Bean
    public DirectChannel fileOutputChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public TailerRestartService getTailerRestart() {
    	return new TailerRestartService(tailer);
    }

    
    @Bean
    public FileTailingMessageProducerSupport getTailer() {
//    	FileTailInboundChannelAdapterFactoryBean factoryBean = new FileTailInboundChannelAdapterFactoryBean();
//    	factoryBean.afterPropertiesSet();
    
    	//tailer = new OSDelegatingFileTailingMessageProducer();
    	tailer = new ApacheCommonsFileTailingMessageProducer();
    	tailer.setTailAttemptsDelay(500);
    	tailer.setAutoStartup(false);
    	
    	tailer.setFile(new File("/Users/mpanchal/file-source/QUOTE_APPROVAL_DETAILS_CAT_6.txt"));
    	tailer.setOutputChannel(source.output());
		
		return tailer;
	}

//	@Bean
//	public IntegrationFlow tailFilesFlow() throws Exception {
//		return IntegrationFlows.from(getTailer())
//				.channel(source.output())
//				.get();
//	}

//    @Bean
//    public IntegrationFlow tailFlow() {
//
//        return IntegrationFlows.from((MessageProducers p) -> p.tail(new File(properties.getFilename()))
//                                                                            .delay(500)
//                                                                            .end(false)
//                                                                            .autoStartup(true))
//                                                            .channel(source.output())
//                                                            .get();
//    }
    
    @Bean
//    @InboundChannelAdapter(value="directoryChannel", poller = @Poller(fixedDelay="100"))
    public MessageSource<File> fileReadingMessageSource() {
    	
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(new SimplePatternFileListFilter("QUOTE_APPROVAL_DETAILS_CAT*"));
//        filters.addFilter(new RegexPatternFileListFilter("(?:\..*(?!\/))+QUOTE_APPROVAL_DETAILS_CAT*"));
        //filters.addFilter(new AcceptOnceFileListFilter());

        //filters.addFilter(new LastModifiedFileListFilter());

        FileReadingMessageSource fileSource = new FileReadingMessageSource();

        String filePath = "/Users/mpanchal/file-source";

        fileSource.setDirectory(new File(filePath));
        fileSource.setFilter(filters);
        fileSource.setScanEachPoll(true);
        fileSource.setUseWatchService(true);
        fileSource.setWatchEvents(FileReadingMessageSource.WatchEventType.CREATE,
        						FileReadingMessageSource.WatchEventType.MODIFY);

        return fileSource;
    }

    @Bean
    public IntegrationFlow readDirectoryFlow() {
    	
        return IntegrationFlows.from(
        		//"directoryChannel")
        		fileReadingMessageSource(), 
        		e -> e.poller(Pollers.cron("*/10 * * * * *")))
        		//e -> e.poller(Pollers.fixedDelay(1)
        		//					 .maxMessagesPerPoll(0))
        		//.transform(Transformers.fileToString())
                //.channel(fileInputChannel())
                .handle(tailerRestart)
                //.channel(source.output())
                .handle(System.out::println)
                .get();
    }

    

//    @ServiceActivator
//    public void restartTailer(File input) throws Exception {
//    	tailer.stop();
//    	tailer.setFile(input);
//    	tailer.start();
//    }
    
    
    
//  @Bean
//  public IntegrationFlow restartFlow() {
//  	
//      return IntegrationFlows.from(fileInputChannel())
//              .handle(myservice)
//              .get();
//  }
    
    //source.setAutoCreateDirectory(true);
//  LocalDateTime currentDate = LocalDateTime.now();
//  filePath = filePath + "/" + currentDate.getMonthValue()
//                            + "." + currentDate.getYear()
//                            + "/" + currentDate.getDayOfMonth() ;
    
}
