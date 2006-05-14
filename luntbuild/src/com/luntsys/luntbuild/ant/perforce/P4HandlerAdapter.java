/*
 * Copyright  2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.luntsys.luntbuild.ant.perforce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
/**
 * base class to manage streams around the execution of the Perforce
 * command line client
 *
 * @author <A HREF="mailto:leslie.hughes@rubus.com">Les Hughes</A>
 *
 * Modified by <a href="mailto:yjshen@yahoo.com">alvin shen</a> to seperate
 * the handling of output lines and error lines
 */
public abstract class P4HandlerAdapter  implements P4Handler {

    String p4input = "";
    private PumpStreamHandler myHandler = null;
	P4OutputStream p4Stdout = null;
	P4OutputStream p4Stderr = null;

    /**
     *  set any data to be written to P4's stdin
     *  @param p4Input the text to write to P4's stdin
     */
    public void setOutput(String p4Input) {
        this.p4input = p4Input;
    }
    /**
     * subclasses of P4HandlerAdapter must implement this routine
     * processing of one line of stdout
     * @param line line of stdout or stderr to process
     */
    public abstract void processStdout(String line);

	/**
	 * subclass of P4HandlerAdapter must implement this routine
	 * processing one line of stderr
	 * @param line
	 */
	public abstract void processStderr(String line);

    /**
     * this routine gets called by the execute routine of the Execute class
     * it connects the PumpStreamHandler to the input/output/error streams of the process.
     * @throws BuildException
     * @see org.apache.tools.ant.taskdefs.Execute#execute
     */
    public void start() throws BuildException {
		p4Stdout = new P4OutputStream(){
			public void process(String line){
				processStdout(line);
			}
		};
		p4Stderr = new P4OutputStream(){
			public void process(String line){
				processStderr(line);
			}
		};
        if (p4input != null && p4input.length() > 0) {
            myHandler = new PumpStreamHandler(p4Stdout, p4Stderr,
                new ByteArrayInputStream(p4input.getBytes()));
        } else {
            myHandler = new PumpStreamHandler(p4Stdout, p4Stderr);
        }
        myHandler.setProcessInputStream(os);
        myHandler.setProcessErrorStream(es);
        myHandler.setProcessOutputStream(is);
        myHandler.start();
    }

    /**
     * stops the processing of streams
     * called from P4Base#execP4Command(String command, P4Handler handler)
     * @see P4Base#execP4Command(String, P4Handler)
     */
    public void stop() {
		if (myHandler != null)
        	myHandler.stop();
		try {
			if (p4Stdout != null){
				p4Stdout.close();
				p4Stdout = null;
			}
			if (p4Stderr != null){
				p4Stderr.close();
				p4Stderr = null;
			}
		} catch (IOException e) {
			// ignore the error
		}
	}

    OutputStream os;    //Input
    InputStream is;     //Output
    InputStream es;     //Error

    /**
     * connects the handler to the input stream into Perforce
     * used indirectly by tasks requiring to send specific standard input
     * such as p4label, p4change
     * @param os the stream bringing input to the p4 executable
     * @throws IOException under unknown circumstances
     */
    public void setProcessInputStream(OutputStream os) throws IOException {
        this.os = os;
    }

    /**
     * connects the handler to the stderr of the Perforce process
     * @param is stderr coming from Perforce
     * @throws IOException under unknown circumstances
     */
    public void setProcessErrorStream(InputStream is) throws IOException {
        this.es = is;
    }

    /**
     * connects the handler to the stdout of the Perforce process
     * @param is stdout coming from Perforce
     * @throws IOException under unknown circumstances
     */
    public void setProcessOutputStream(InputStream is) throws IOException {
        this.is = is;
    }
}
