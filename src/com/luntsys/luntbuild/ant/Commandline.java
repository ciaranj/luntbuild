/*
 * Copyright  2000-2004 The Apache Software Foundation
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

package com.luntsys.luntbuild.ant;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.StringUtils;


/**
 * <code>CommandLine</code> objects help with handling command lines specifying processes to
 * execute.
 *
 * The class can be used to define a command line as nested elements or as a
 * helper to define a command line by an application.
 * <p>
 * <code>
 * &lt;someelement&gt;<br>
 * &nbsp;&nbsp;&lt;acommandline executable="/executable/to/run"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;argument value="argument 1" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;argument line="argument_1 argument_2 argument_3" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;argument value="argument 4" /&gt;<br>
 * &nbsp;&nbsp;&lt;/acommandline&gt;<br>
 * &lt;/someelement&gt;<br>
 * </code>
 * The element <code>someelement</code> must provide a method
 * <code>createAcommandline</code> which returns an instance of this class.
 *
 * @author thomas.haas@softwired-inc.com
 * @author Stefan Bodewig
 */
public class Commandline implements Cloneable {

    /**
     * The arguments of the command
     */
    private Vector arguments = new Vector();

    /**
     * the program to execute
     */
    private String executable = null;

    protected static final String DISCLAIMER =
        StringUtils.LINE_SEP
        + "The \' characters around the executable and arguments are"
        + StringUtils.LINE_SEP
        + "not part of the command."
        + StringUtils.LINE_SEP;

    /**
     * Create a <code>CommandLine</code> from a string.
     * 
     * @param toProcess the command line. The first element becomes the executable, the rest
     * become the arguments.
     */
    public Commandline(String toProcess) {
        super();
        String[] tmp = translateCommandline(toProcess);
        if (tmp != null && tmp.length > 0) {
            setExecutable(tmp[0]);
            for (int i = 1; i < tmp.length; i++) {
                createArgument().setValue(tmp[i]);
            }
        }
    }

    /**
     *  Create an empty <code>CommandLine</code>.
     */
    public Commandline() {
        super();
    }

    /**
     * Used for nested XML command line definitions.
     */
    public static class Argument extends ProjectComponent {

        private String[] parts;

		private String[] descriptiveParts;

        /**
         * Sets a single command line argument.
         *
         * @param value a single command line argument
         */
        public void setValue(String value) {
            parts = new String[] {value};
        }

		/**
		 * Sets the descriptive value.
		 * 
		 * @param descriptiveValue the descriptive value
		 */
		public void setDescriptiveValue(String descriptiveValue) {
			descriptiveParts = new String[] {descriptiveValue};
		}

        /**
         * Sets the line of arguments.  The arguments will be split.
         *
         * @param line the line of arguments
         */
        public void setLine(String line) {
            if (line == null) {
                return;
            }
            parts = translateCommandline(line);
        }

		/**
		 * Sets the line of descriptive values.  The values will be split.
		 * 
		 * @param descriptiveLine the line of values
		 */
		public void setDescriptiveLine(String descriptiveLine) {
			if (descriptiveLine == null) {
				return;
			}
			descriptiveParts = translateCommandline(descriptiveLine);
		}

        /**
         * Sets a single command line argument and treats it like a
         * PATH - ensures the right separator for the local platform
         * is used.
         *
         * @param value a single commandline argument
         */
        public void setPath(Path value) {
            parts = new String[] {value.toString()};
        }

        /**
         * Sets a single command line argument from a reference to a
         * path - ensures the right separator for the local platform
         * is used.
         *
         * @param value a single command line argument
         */
        public void setPathref(Reference value) {
            Path p = new Path(getProject());
            p.setRefid(value);
            parts = new String[] {p.toString()};
        }

        /**
         * Sets a single command line argument to the absolute filename
         * of the given file.
         *
         * @param value a single command line argument
         */
        public void setFile(File value) {
            parts = new String[] {value.getAbsolutePath()};
        }

        /**
         * Returns the parts of this argument.
         * 
         * @return the parts
         */
        public String[] getParts() {
            return parts;
        }

		/**
		 * Gets the descriptive values of this argument.
		 * 
		 * @return the descriptive values
		 */
		public String[] getDescriptiveParts() {
			return descriptiveParts;
		}
    }

    /**
     * Class to keep track of the position of an Argument.
     * <p>This class is there to support the srcfile and targetfile
     * elements of &lt;execon&gt; and &lt;transform&gt; - don't know
     * whether there might be additional use cases.</p> --SB
     */
    public class Marker {

        private int position;
        private int realPos = -1;

		/**
		 * Create a new marker at a position.
		 * 
		 * @param position the position
		 */
        Marker(int position) {
            this.position = position;
        }

        /**
         * Return the number of arguments that preceeded this marker.
         *
         * <p>The name of the executable - if set - is counted as the
         * very first argument.</p>
         * 
         * @return the number of arguments
         */
        public int getPosition() {
            if (realPos == -1) {
                realPos = (executable == null ? 0 : 1);
                for (int i = 0; i < position; i++) {
                    Argument arg = (Argument) arguments.elementAt(i);
                    realPos += arg.getParts().length;
                }
            }
            return realPos;
        }
    }

    /**
     * Creates an argument object.
     *
     * <p>Each <code>CommandLine</code> object has at most one instance of the
     * argument class.  This method calls
     * <code>this.createArgument(false)</code>.</p>
     *
     * @return the argument object
     * @see #createArgument(boolean)
     */
    public Argument createArgument() {
        return this.createArgument(false);
    }

    /**
     * Creates an argument object and adds it to our list of args.
     *
     * <p>Each <code>CommandLine</code> object has at most one instance of the
     * <code>Argument</code> class.</p>
     *
     * @param insertAtStart if <code>true</code>, the argument is inserted at the
     * beginning of the list of args, otherwise it is appended
     * @return the argument object
     */
    public Argument createArgument(boolean insertAtStart) {
        Argument argument = new Argument();
        if (insertAtStart) {
            arguments.insertElementAt(argument, 0);
        } else {
            arguments.addElement(argument);
        }
        return argument;
    }

    /**
     * Sets the executable to run. All file separators in the string
     * are converted to the platform specific value.
     * 
     * @param executable the executable
     */
    public void setExecutable(String executable) {
        if (executable == null || executable.length() == 0) {
            return;
        }
        this.executable = executable;
    }


    /**
     * Get the executable to run.
     * 
     * @return the program to run, <code>null</code> if not yet set
     */
    public String getExecutable() {
        return executable;
    }


    /**
     * Append arguments to the existing command.
     * 
     * @param line an array of arguments to append
     */
    public void addArguments(String[] line) {
        for (int i = 0; i < line.length; i++) {
            createArgument().setValue(line[i]);
        }
    }

    /**
     * Returns the executable and all defined arguments.
     * 
     * @return the executable and arguments
     */
    public String[] getCommandline() {
        List commands = new LinkedList();
        ListIterator list = commands.listIterator();
        addCommandToList(list);
        final String[] result = new String[commands.size()];
        return (String[]) commands.toArray(result);
    }

	/**
	 * Returns the executable and all defined arguments in descriptional form.
	 * 
	 * @return the executable and all arguments
	 */
	public String[] getDescriptiveCommandline() {
		List commands = new LinkedList();
		ListIterator list = commands.listIterator();
		addDescriptiveCommandToList(list);
		final String[] result = new String[commands.size()];
		return (String[]) commands.toArray(result);
	}

    /**
     * Add the entire command, including (optional) executable to a list.
     * 
     * @param list
     * @since Ant 1.6
     */
    public void addCommandToList(ListIterator list) {
        if (executable != null) {
            list.add(executable);
        }
        addArgumentsToList(list);
    }

	/**
	 * Add the entire command, including (optional) executable to a list.
	 * 
	 * @param list
	 * @since Ant 1.6
	 */
	public void addDescriptiveCommandToList(ListIterator list) {
		if (executable != null) {
			list.add(executable);
		}
		addDescriptiveArgumentsToList(list);
	}

    /**
     * Returns all arguments defined by <code>addLine</code>,
     * <code>addValue</code> or the argument object.
     * 
     * @return the arguments
     */
    public String[] getArguments() {
        List result = new ArrayList(arguments.size() * 2);
        addArgumentsToList(result.listIterator());
        String [] res = new String[result.size()];
        return (String[]) result.toArray(res);
    }

    /**
     * Returns all arguments defined by <code>addLine</code>,
     * <code>addValue</code> or the argument object in descriptional form.
     * 
     * @return the arguments
     */
	public String[] getDescriptiveArguments() {
		List result = new ArrayList(arguments.size() * 2);
		addDescriptiveArgumentsToList(result.listIterator());
		String [] res = new String[result.size()];
		return (String[]) result.toArray(res);
	}

    /**
     * Appends all the arguments to the tail of a supplied list.
     * 
     * @param list the list of arguments to add to
     * @since Ant 1.6
     */
    public void addArgumentsToList(ListIterator list) {
        for (int i = 0; i < arguments.size(); i++) {
            Argument arg = (Argument) arguments.elementAt(i);
            String[] s = arg.getParts();
            if (s != null) {
                for (int j = 0; j < s.length; j++) {
                    list.add(s[j]);
                }
            }
        }
    }

    /**
     * Appends all the arguments to the tail of a supplied list in descriptional form.
     * 
     * @param list the list of arguments to add to
     */
	public void addDescriptiveArgumentsToList(ListIterator list) {
		for (int i = 0; i < arguments.size(); i++) {
			Argument arg = (Argument) arguments.elementAt(i);
			String[] s = arg.getDescriptiveParts();
			if (s == null)
				s = arg.getParts();
			if (s != null) {
				for (int j = 0; j < s.length; j++) {
					list.add(s[j]);
				}
			}
		}
	}

    /**
     * Stringify operator returns the command line as a string.
     * 
     * @return the command line
     */
    public String toString() {
        return toString(getCommandline());
    }

    /**
     * Put quotes around the given String if necessary.
     *
     * <p>If the argument doesn't include spaces or quotes, return it
     * as is. If it contains double quotes, use single quotes - else
     * surround the argument by double quotes.</p>
     * 
     * @param argument the argument
     * @return the argument with any necessary quotes
     * @exception org.apache.tools.ant.BuildException if the argument contains both, single
     *                           and double quotes.
     */
    public static String quoteArgument(String argument) {
        if (argument.indexOf("\"") > -1) {
            if (argument.indexOf("\'") > -1) {
                throw new BuildException("Can\'t handle single and double"
                        + " quotes in same argument");
            } else {
                return '\'' + argument + '\'';
            }
        } else if (argument.indexOf("\'") > -1 || argument.indexOf(" ") > -1) {
            return '\"' + argument + '\"';
        } else {
            return argument;
        }
    }

    /**
     * Quotes the parts of the given array in way that makes them
     * usable as command line arguments.
     * 
     * @param line the list of arguments
     * @return empty string for <code>null</code> or no command, else every argument split
     * by spaces and quoted by quoting rules
     */
    public static String toString(String [] line) {
        // empty path return empty string
        if (line == null || line.length == 0) {
            return "";
        }

        // path containing one or more elements
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < line.length; i++) {
            if (i > 0) {
                result.append(' ');
            }
            result.append(quoteArgument(line[i]));
        }
        return result.toString();
    }

    /**
     * Crack a command line.
     * 
     * @param toProcess the command line to process
     * @return the command line broken into strings
     * An empty or <code>null</code> toProcess parameter results in a zero sized array
     */
    public static String[] translateCommandline(String toProcess) {
        if (toProcess == null || toProcess.length() == 0) {
            //no command? no string
            return new String[0];
        }

        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        StringTokenizer tok = new StringTokenizer(toProcess, "\"\' ", true);
        Vector v = new Vector();
        StringBuffer current = new StringBuffer();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            String nextTok = tok.nextToken();
            switch (state) {
            case inQuote:
                if ("\'".equals(nextTok)) {
                    lastTokenHasBeenQuoted = true;
                    state = normal;
                } else {
                    current.append(nextTok);
                }
                break;
            case inDoubleQuote:
                if ("\"".equals(nextTok)) {
                    lastTokenHasBeenQuoted = true;
                    state = normal;
                } else {
                    current.append(nextTok);
                }
                break;
            default:
                if ("\'".equals(nextTok)) {
                    state = inQuote;
                } else if ("\"".equals(nextTok)) {
                    state = inDoubleQuote;
                } else if (" ".equals(nextTok)) {
                    if (lastTokenHasBeenQuoted || current.length() != 0) {
                        v.addElement(current.toString());
                        current = new StringBuffer();
                    }
                } else {
                    current.append(nextTok);
                }
                lastTokenHasBeenQuoted = false;
                break;
            }
        }

        if (lastTokenHasBeenQuoted || current.length() != 0) {
            v.addElement(current.toString());
        }

        if (state == inQuote || state == inDoubleQuote) {
            throw new BuildException("unbalanced quotes in " + toProcess);
        }

        String[] args = new String[v.size()];
        v.copyInto(args);
        return args;
    }

    /**
     * Size operator. This actually creates the command line, so it is not
     * a zero cost operation.
     * 
     * @return number of elements in the command, including the executable
     */
    public int size() {
        return getCommandline().length;
    }

    /**
     * A deep clone of the contained object.
     * 
     * @return the clone of the contained object
     */
    public Object clone() {
        try {
            Commandline c = (Commandline) super.clone();
            c.arguments = (Vector) arguments.clone();
            return c;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Clear out the whole command line.
     */
    public void clear() {
        executable = null;
        arguments.removeAllElements();
    }

    /**
     * Clear out the arguments but leave the executable in place for
     * another operation.
     */
    public void clearArgs() {
        arguments.removeAllElements();
    }

    /**
     * Creates a marker.
     *
     * <p>This marker can be used to locate a position on the
     * commandline - to insert something for example - when all
     * parameters have been set.</p>
     * 
     * @return the marker
     */
    public Marker createMarker() {
        return new Marker(arguments.size());
    }

    /**
     * Returns a string that describes the command and arguments
     * suitable for verbose output before a call to
     * <code>Runtime.exec(String[])<code>
     * 
     * @return the command and arguments
     * @since Ant 1.5
     */
    public String describeCommand() {
        return describeCommand(this);
    }

    /**
     * Returns a string that describes the arguments suitable for
     * verbose output before a call to
     * <code>Runtime.exec(String[])<code>
     * 
     * @return the arguments
     * @since Ant 1.5
     */
    public String describeArguments() {
        return describeArguments(this);
    }

    /**
     * Returns a string that describes the command and arguments
     * suitable for verbose output before a call to
     * <code>Runtime.exec(String[])<code>
     * 
     * @param line the command line
     * @return the command and arguments
     * @since Ant 1.5
     */
    public static String describeCommand(Commandline line) {
        return describeCommand(line.getDescriptiveCommandline());
    }


	/**
     * Returns a string that describes the arguments suitable for
     * verbose output before a call to
     * <code>Runtime.exec(String[])<code>
     * 
     * @param line the command line
     * @return the arguments
     * @since Ant 1.5
     */
    public static String describeArguments(Commandline line) {
        return describeArguments(line.getDescriptiveArguments());
    }

    /**
     * Returns a string that describes the command and arguments
     * suitable for verbose output before a call to
     * <code>Runtime.exec(String[])<code>.
     *
     * <p>This method assumes that the first entry in the array is the
     * executable to run.</p>
     * 
     * @param args the command and arguments
     * @return the command and arguments
     * @since Ant 1.5
     */
    public static String describeCommand(String[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        StringBuffer buf = new StringBuffer("Executing \'");
        buf.append(args[0]);
        buf.append("\'");
        if (args.length > 0) {
            buf.append(" with ");
            buf.append(describeArguments(args, 1));
        } else {
            buf.append(DISCLAIMER);
        }
        return buf.toString();
    }

    /**
     * Returns a string that describes the arguments suitable for
     * verbose output before a call to
     * <code>Runtime.exec(String[])<code>
     * 
     * @param args the arguments
     * @return the arguments
     * @since Ant 1.5
     */
    public static String describeArguments(String[] args) {
        return describeArguments(args, 0);
    }

    /**
     * Returns a string that describes the arguments suitable for
     * verbose output before a call to
     * <code>Runtime.exec(String[])<code>
     * 
     * @param args the arguments
     * @param offset ignore entries before this index
     * @return the arguments
     * @since Ant 1.5
     */
    protected static String describeArguments(String[] args, int offset) {
        if (args == null || args.length <= offset) {
            return "";
        }

        StringBuffer buf = new StringBuffer("argument");
        if (args.length > offset) {
            buf.append("s");
        }
        buf.append(":").append(StringUtils.LINE_SEP);
        for (int i = offset; i < args.length; i++) {
            buf.append("\'").append(args[i]).append("\'")
                .append(StringUtils.LINE_SEP);
        }
        buf.append(DISCLAIMER);
        return buf.toString();
    }
}
