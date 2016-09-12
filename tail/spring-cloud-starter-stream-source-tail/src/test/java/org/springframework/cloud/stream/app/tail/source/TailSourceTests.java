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

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.integration.file.splitter.FileSplitter;
import org.springframework.integration.json.JsonPathUtils;
import org.springframework.integration.support.json.JsonObjectMapperProvider;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TailSourceApplication.class)
@DirtiesContext
public abstract class TailSourceTests {


    @Autowired
    protected Source source;

    @Autowired
    protected MessageCollector messageCollector;


    @IntegrationTest({})
    public static class payloadTest extends TailSourceTests {

        @Test
        public void otherTest() throws Exception
        {
            Message<?> received= messageCollector.forChannel(source.output()).poll(10, TimeUnit.SECONDS);
            System.out.println("Received ="+received);
        }

        @Test
        public void testSimpleFile() throws Exception {
            int i = messageCollector.forChannel(source.output()).size();
            System.out.println("Size = +"+i);
            Message<?> received= messageCollector.forChannel(source.output()).poll(10, TimeUnit.SECONDS);
            for (Iterator<Message<?>> iter = messageCollector.forChannel(source.output()).iterator(); iter.hasNext();)
                    {
                System.out.println("value ="+iter.next().getPayload());
                    }
            System.out.println("RECEIVED = "+received);
            Assert.notNull(received);
        }
    }


}
