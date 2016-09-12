/*
 * Copyright 2015 the original author or authors.
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.AssertTrue;
import java.io.File;
import java.util.regex.Pattern;

@ConfigurationProperties("tail")
public class TailSourceProperties {

    /**
     * The file name to tail
     */
    @Value("#{ systemProperties['tail.file'] ?: '/tmp/tailfile.log'}")
    private String filename;

     /**
     * the native options to be used in conjunction with the tail command eg. -F -n 0 etc.
     */
    @Value("#{ systemProperties['tail.nativeoptions'] ?: '-F -n 0'}")
    private String nativeOptions;

    public String getNativeOptions() {
        return nativeOptions;
    }

    public void setNativeOptions(String nativeOptions) {
        this.nativeOptions = nativeOptions;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
