/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-3-3
 * Time: 21:42:21
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
package com.luntsys.luntbuild.dependency;

import java.util.*;

/**
 * This class handles various dependency resolving for given object(s), such as resolving
 * correct execution order of a set of dependency objects, etc.
 *
 * @author robin shine
 */
public class DependencyResolver {
	private Set visitingNodes;
	private Set remainedNodes;
	private Object lock = new Object();
	private Object userData4DependsOn= null;
	private Object userData4Visit = null;

	public DependencyResolver(Object userData4DependsOn, Object userData4Visit) {
		this.userData4DependsOn = userData4DependsOn;
		this.userData4Visit = userData4Visit;
	}

	public void visitNodesThisNodeDependsOnRecursively(DependentNode thisNode) throws DependencyLoopException {
		visitNodes(findNodesThisNodeDependsOnRecursively(thisNode));
	}

	public void visitNodesDependsOnThisNodeRecursively(Set nodes, DependentNode thisNode) throws DependencyLoopException {
		visitNodes(findNodesDependsOnThisNodeRecursively(nodes, thisNode));
	}

	public void visitNodes(Set nodes) throws DependencyLoopException {
		synchronized (lock) {
			remainedNodes = new HashSet(nodes);
			visitingNodes = new HashSet();
			while (!remainedNodes.isEmpty()) {
				DependentNode leaf;
				while ((leaf = findLeafNode(remainedNodes, visitingNodes)) != null) {
					visitingNodes.add(leaf);
					new Thread(new NodeRunner(leaf)).start();
				}
				if (visitingNodes.isEmpty())
					throw new DependencyLoopException(DependencyLoopException.getMessage(remainedNodes));
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// ignores
				}
			}
		}
	}

	public DependentNode findLeafNode(Set nodes, Set excludedNodes) {
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			DependentNode node = (DependentNode) it.next();
			if (excludedNodes.contains(node))
				continue;
			boolean isLeaf = true;
			Iterator itDependent = node.getDependsOn(userData4DependsOn).iterator();
			while (itDependent.hasNext()) {
				DependentNode dependentNode = (DependentNode) itDependent.next();
				if (nodes.contains(dependentNode)) {
					isLeaf = false;
					break;
				}
			}
			if (isLeaf)
				return node;
		}
		return null;
	}

	public DependentNode findLeafNode(Set nodes) {
		return findLeafNode(nodes, new HashSet());
	}

	public Set findNodesDependsOnThisNodeRecursively(Set nodes, DependentNode thisNode) {
		Set nodesDependsOnThis = new HashSet(nodes);
		nodesDependsOnThis.removeAll(findNodesThisNodeDependsOnRecursively(thisNode));
		nodesDependsOnThis.add(thisNode);
		Set excludedNodes = new HashSet();
		excludedNodes.add(thisNode);
		DependentNode leafNode;
		while ((leafNode = findLeafNode(nodesDependsOnThis, excludedNodes)) != null)
			nodesDependsOnThis.remove(leafNode);
		nodesDependsOnThis.remove(thisNode);
		return nodesDependsOnThis;
	}

	public void detectDependencyLoop(Set nodes) throws DependencyLoopException {
		Set availableNodes = new HashSet(nodes);
		while (!availableNodes.isEmpty()) {
			DependentNode leafNode = findLeafNode(availableNodes);
			if (leafNode != null) {
				availableNodes.remove(leafNode);
				continue;
			}
			throw new DependencyLoopException(DependencyLoopException.getMessage(availableNodes));
		}
	}

	public Set findNodesThisNodeDependsOnRecursively(DependentNode thisNode) {
		Set nodes = new HashSet();
		nodes.add(thisNode);
		boolean isNodeAdded = true;
		while (isNodeAdded) {
			isNodeAdded = false;
			Iterator it = nodes.iterator();
			while (it.hasNext()) {
				DependentNode node = (DependentNode) it.next();
				Iterator itDependent = node.getDependsOn(userData4DependsOn).iterator();
				while (itDependent.hasNext()) {
					DependentNode dependentNode = (DependentNode) itDependent.next();
					if (!nodes.contains(dependentNode)) {
						nodes.add(dependentNode);
						isNodeAdded = true;
					}
				}
				if (isNodeAdded)
					break;
			}
		}
		nodes.remove(thisNode);
		return nodes;
	}

	public class NodeRunner implements Runnable {
		private DependentNode node;

		public NodeRunner(DependentNode node) {
			this.node = node;
		}

		public void run() {
			node.visit(userData4Visit);
			synchronized (lock) {
				remainedNodes.remove(node);
				visitingNodes.remove(node);
				lock.notify();
			}
		}
	}
}
