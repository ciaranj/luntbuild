/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-11-4
 * Time: 13:09:08
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
package com.luntsys.luntbuild.test;

import com.luntsys.luntbuild.dependency.DependencyLoopException;
import com.luntsys.luntbuild.dependency.DependencyResolver;
import com.luntsys.luntbuild.dependency.DependentNode;
import com.luntsys.luntbuild.facades.Constants;

import junit.framework.TestCase;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.*;

/**
 * This class tests functionalities of {@link com.luntsys.luntbuild.dependency.DependencyResolver}
 * @author robin shine
 */
public class TestDependencyResolver extends TestCase {
	private Set nodes = new HashSet();
	private List visitOrder = new ArrayList();

	public void testDetectDependencyLoop() {
    	nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3"}));
		nodes.add(new DependencyNodeImpl("3", new String[0]));
		try {
			new DependencyResolver(null, null).detectDependencyLoop(nodes);
		} catch (DependencyLoopException e) {
			fail();
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"1"}));
		try {
			new DependencyResolver(null, null).detectDependencyLoop(nodes);
			fail();
		} catch (DependencyLoopException e) {
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3"}));
		nodes.add(new DependencyNodeImpl("3", new String[]{"2"}));
		try {
			new DependencyResolver(null, null).detectDependencyLoop(nodes);
			fail();
		} catch (DependencyLoopException e) {
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3"}));
		nodes.add(new DependencyNodeImpl("3", new String[]{"1"}));
		try {
			new DependencyResolver(null, null).detectDependencyLoop(nodes);
			fail();
		} catch (DependencyLoopException e) {
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[0]));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3"}));
		nodes.add(new DependencyNodeImpl("3", new String[]{"2"}));
		try {
			new DependencyResolver(null, null).detectDependencyLoop(nodes);
			fail();
		} catch (DependencyLoopException e) {
		}
	}

	public void testVisitNodesThisNodeDependsOnRecursively() {
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("2", new String[0]));
		nodes.add(new DependencyNodeImpl("3", new String[]{"2"}));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesThisNodeDependsOnRecursively(new DependencyNodeImpl("1", new String[]{"2", "3"}));
			assertEquals(new String[]{"2", "3"}, visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("3", new String[0]));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3"}));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesThisNodeDependsOnRecursively(new DependencyNodeImpl("1", new String[]{"2", "3"}));
			assertEquals(new String[]{"3", "2"}, visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("5", new String[]{"1", "3"}));
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3", "4"}));
		nodes.add(new DependencyNodeImpl("3", new String[0]));
		nodes.add(new DependencyNodeImpl("4", new String[0]));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesThisNodeDependsOnRecursively(new DependencyNodeImpl("5", new String[]{"1", "3"}));
			visitOrder.remove(0);
			visitOrder.remove(0);
			assertEquals(new String[]{"2", "1"}, visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesThisNodeDependsOnRecursively(new DependencyNodeImpl("1", new String[]{"2", "3"}));
			visitOrder.remove(0);
			visitOrder.remove(0);
			assertEquals(new String[]{"2"}, visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"1"}));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesThisNodeDependsOnRecursively(new DependencyNodeImpl("1", new String[]{"2"}));
			assertEquals(new String[]{"2"}, visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3"}));
		nodes.add(new DependencyNodeImpl("3", new String[]{"2"}));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesThisNodeDependsOnRecursively(new DependencyNodeImpl("1", new String[]{"2"}));
			fail();
		} catch (DependencyLoopException e) {
		}
	}

	public void testVisitNodesDependsOnThisNodeRecursively() {
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("2", new String[0]));
		nodes.add(new DependencyNodeImpl("3", new String[]{"2"}));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesDependsOnThisNodeRecursively(nodes, new DependencyNodeImpl("2", new String[0]));
			assertEquals(new String[]{"3", "1"}, visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}

		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("3", new String[0]));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3"}));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesDependsOnThisNodeRecursively(nodes, new DependencyNodeImpl("2", new String[]{"3"}));
			assertEquals(new String[]{"1"}, visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("5", new String[]{"1", "3"}));
		nodes.add(new DependencyNodeImpl("1", new String[]{"2", "3"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"3", "4"}));
		nodes.add(new DependencyNodeImpl("3", new String[0]));
		nodes.add(new DependencyNodeImpl("4", new String[0]));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesDependsOnThisNodeRecursively(nodes, new DependencyNodeImpl("3", new String[0]));
			assertEquals(new String[]{"2", "1", "5"}, visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("1", new String[]{"2"}));
		nodes.add(new DependencyNodeImpl("2", new String[]{"1"}));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesDependsOnThisNodeRecursively(nodes, new DependencyNodeImpl("1", new String[]{"2"}));
			assertEquals(new String[0], visitOrder);
		} catch (DependencyLoopException e) {
        	fail();
		}
		nodes.clear();
		nodes.add(new DependencyNodeImpl("2", new String[]{"3"}));
		nodes.add(new DependencyNodeImpl("3", new String[]{"2", "4"}));
		nodes.add(new DependencyNodeImpl("4", new String[0]));
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesDependsOnThisNodeRecursively(nodes, new DependencyNodeImpl("4", new String[0]));
			fail();
		} catch (DependencyLoopException e) {
		}
		try {
			visitOrder.clear();
			new DependencyResolver(null, null).visitNodesDependsOnThisNodeRecursively(nodes, new DependencyNodeImpl("3", new String[]{"2", "4"}));
			assertEquals(new String[0], visitOrder);
		} catch (DependencyLoopException e) {
			fail();
		}
	}

	private void assertEquals(String[] expected, List actual) {
		if (expected.length != actual.size())
			fail();
		for (int i=0; i<expected.length; i++) {
			if (!expected[i].equals(actual.get(i)))
				 fail();
		}
	}

	public class DependencyNodeImpl implements DependentNode {
		private String name;
		private String nameDependsOn[];

		public DependencyNodeImpl(String name, String[] dependsOn) {
			this.name = name;
            this.nameDependsOn = dependsOn;
		}

		public Set getDependsOn(Object userData) {
			Set nodesDependsOn = new HashSet();
			for (int i=0; i<nameDependsOn.length; i++) {
				DependentNode dependentNode = new DependencyNodeImpl(nameDependsOn[i], new String[0]);
				Iterator it = nodes.iterator();
				while (it.hasNext()) {
					DependentNode node = (DependentNode) it.next();
					if (node.equals(dependentNode)) {
						nodesDependsOn.add(node);
						break;
					}
				}
			}
			return nodesDependsOn;
		}

		public void visit(Object userData) {
			visitOrder.add(getName());
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			return new ToStringBuilder(this)
				.append("name", getName())
				.toString();
		}

		public boolean equals(Object other) {
			if ( !(other instanceof DependencyNodeImpl) ) return false;
			DependencyNodeImpl castOther = (DependencyNodeImpl) other;
			return new EqualsBuilder()
				.append(this.getName(), castOther.getName()).isEquals();
		}

		public int hashCode() {
			return new HashCodeBuilder()
				.append(getName()).toHashCode();
		}

        /**
         * Get dependent triggering strategy, should be one value of
         * @return one value of
         * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_ALL_DEPENDENT_SCHEDULES},
         * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_NONE_DEPENDENT_SCHEDULES},
         * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_DEPENDS_ON_THIS},
         * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_THIS_DEPENDS_ON}

         */
        public int getTriggerDependencyStrategy() {
            return Constants.TRIGGER_ALL_DEPENDENT_SCHEDULES;
        }

	}
}
