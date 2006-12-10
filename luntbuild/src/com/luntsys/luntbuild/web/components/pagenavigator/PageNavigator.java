/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-29
 * Time: 6:46:18
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package com.luntsys.luntbuild.web.components.pagenavigator;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IRequestCycle;

/**
 * This component shows off a set of links which can be used to navigate between pages,
 * and an information about current page and total pages. It seems like:
 * page 1 of 4 << < > >>
 * @author robin shine
 */
public abstract class PageNavigator extends BaseComponent{
	public void firstPage(IRequestCycle cycle){
		int requestNavigateCount = ((Integer)cycle.getServiceParameters()[0]).intValue();
		if (requestNavigateCount == getNavigateCount()){
			setCurrentPage(1);
			setNavigateCount(getNavigateCount() + 1);
		}
        getListener().actionTriggered(this, cycle);
	}

	public void previousPage(IRequestCycle cycle){
		int requestNavigateCount = ((Integer)cycle.getServiceParameters()[0]).intValue();
		if (requestNavigateCount == getNavigateCount()){
			setNavigateCount(getNavigateCount() + 1);
			int currentPage = getCurrentPage() - 1;
			if (currentPage >= 1)
				setCurrentPage(currentPage);
		}
		getListener().actionTriggered(this, cycle);
	}

	public void nextPage(IRequestCycle cycle){
		int requestNavigateCount = ((Integer)cycle.getServiceParameters()[0]).intValue();
		if (requestNavigateCount == getNavigateCount()){
			setNavigateCount(getNavigateCount() + 1);
			int currentPage = getCurrentPage() + 1;
			if (currentPage <= getTotalPages())
				setCurrentPage(currentPage);
		}
		getListener().actionTriggered(this, cycle);
	}

	public void lastPage(IRequestCycle cycle){
		int requestNavigateCount = ((Integer)cycle.getServiceParameters()[0]).intValue();
		if (requestNavigateCount == getNavigateCount()){
			setNavigateCount(getNavigateCount() + 1);
			setCurrentPage(getTotalPages());
		}
		getListener().actionTriggered(this, cycle);
	}

	public abstract int getCurrentPage();

	public abstract void setCurrentPage(int currentPage);

	public abstract int getTotalPages();

	public abstract void setTotalPages(int totalPages);

	public abstract IActionListener getListener();

	public abstract void setNavigateCount(int navigateCount);

	public abstract int getNavigateCount();
}
