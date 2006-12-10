/* $Header$
 * 
 * Copyright (c) 2004 - 2005 A.S.E.I. s.r.o.
 */
package com.luntsys.luntbuild.luntclipse.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

/**
 * Highlights a line in the log tab. 
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 289 $   
 * @since 	 0.0.1
 */
public class LogLineHighlighter implements LineStyleListener{
       
    /**
     * @see org.eclipse.swt.custom.LineStyleListener#lineGetStyle(org.eclipse.swt.custom.LineStyleEvent)
     */
    public void lineGetStyle(LineStyleEvent event) {
       if(event.lineText.matches(".*\\[junit\\].*")){
           Color c =  event.display.getSystemColor(SWT.COLOR_BLUE);
           StyleRange range = new StyleRange(event.lineOffset, event.lineText.length(), c, null);
           event.styles = new StyleRange[1];
           event.styles[0] = range;     
       }
    }
   
}
