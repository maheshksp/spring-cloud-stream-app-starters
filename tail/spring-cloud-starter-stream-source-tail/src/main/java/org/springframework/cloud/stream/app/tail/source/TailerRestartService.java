package org.springframework.cloud.stream.app.tail.source;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.tail.FileTailingMessageProducerSupport;
import org.springframework.stereotype.Service;

@Service
public class TailerRestartService {
	
    @Autowired
    FileTailingMessageProducerSupport tailer;

	
	@ServiceActivator
    public File newFileHasArrived(File file) {
    	System.out.println("-------------------------- newFileHasArrived :" + file);
    	System.out.println("-------------------------- tailer :" + tailer.getComponentName());
    	System.out.println("-------------------------- tailer :" + tailer.getOutputChannel().toString());

    	this.tailer.stop();
    	this.tailer.setFile(file);
    	this.tailer.start();
    	return file;
    	
    }

	public TailerRestartService(FileTailingMessageProducerSupport _tailer) {
		super();
		this.tailer = _tailer;
	}



}
