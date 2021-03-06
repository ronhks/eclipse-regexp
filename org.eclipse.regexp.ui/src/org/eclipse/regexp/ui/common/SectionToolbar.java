/*******************************************************************************
 * Copyright (c) 2013 Igor Zapletnev
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Igor Zapletnev - initial API and Implementation
 *******************************************************************************/
package org.eclipse.regexp.ui.common;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Section;

public class SectionToolbar {
	private final ToolBarManager toolBarManager;
	private final ToolBar toolbar;

	public SectionToolbar(final Section section) {
		toolBarManager = new ToolBarManager(SWT.FLAT);
		toolbar = toolBarManager.createControl(section);
		final Cursor handCursor = new Cursor(section.getDisplay(),
				SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);

		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				if (handCursor != null && handCursor.isDisposed() == false) {
					handCursor.dispose();
				}

				toolBarManager.dispose();
			}
		});
		section.setTextClient(toolbar);
	}

	public SectionToolbar add(final IAction action) {
		toolBarManager.add(action);
		return this;
	}

	public SectionToolbar add(final IContributionItem iContributionItem) {
		toolBarManager.add(iContributionItem);
		return this;
	}

	public SectionToolbar done() {
		toolBarManager.update(true);
		return this;
	}

	public void update() {
		toolBarManager.update(true);
	}

	public ToolBar getControl() {
		return toolBarManager.getControl();
	}
}
