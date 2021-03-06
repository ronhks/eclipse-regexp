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
package org.eclipse.regexp.ui.view;

import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.regexp.RegexpEngine;
import org.eclipse.regexp.ui.RegexpUIPlugin;
import org.eclipse.regexp.ui.common.ControlUtils;
import org.eclipse.regexp.ui.view.input.InputListener;
import org.eclipse.regexp.ui.view.input.InputSection;
import org.eclipse.regexp.ui.view.results.ResultsSection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

public class RegularExpressionsView extends ViewPart implements InputListener,
		IPropertyChangeListener, IValueChangeListener {

	private final RegexpEngine engine = new RegexpEngine();
	private final ObservablesManager manager = new ObservablesManager();

	private Text regExp;
	private Text replacement;

	private InputSection input;
	private ResultsSection results;

	public RegularExpressionsView() {
		IPreferenceStore store = RegexpUIPlugin.getDefault()
				.getPreferenceStore();
		store.addPropertyChangeListener(this);
	}

	@Override
	public void createPartControl(final Composite composite) {
		final ManagedForm form = new ManagedForm(composite);
		form.getForm().setText("Regular Expression Test View");

		final FormToolkit toolkit = form.getToolkit();
		toolkit.setBorderStyle(SWT.BORDER);
		toolkit.decorateFormHeading(form.getForm().getForm());
		toolkit.setBorderStyle(SWT.BORDER);

		form.getForm().getToolBarManager().update(true);
		toolkit.decorateFormHeading(form.getForm().getForm());

		final Composite formPanel = form.getForm().getBody();
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(formPanel);

		// general
		toolkit.createLabel(formPanel, "Regular Expression:");
		regExp = toolkit.createText(formPanel, "");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(regExp);
		testObservable(regExp);

		toolkit.createLabel(formPanel, "Replacement:");
		replacement = toolkit.createText(formPanel, "");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(replacement);
		testObservable(replacement);

		// input section
		input = new InputSection();
		input.create(formPanel, toolkit);
		input.addListener(this);

		// results
		results = new ResultsSection();
		results.create(formPanel, toolkit);
	}

	@Override
	public void inputChanged() {
		doTest();
	}

	@Override
	public void handleValueChange(final ValueChangeEvent event) {
		doTest();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		doTest();
	}

	private void testObservable(final Widget toObserve) {
		IObservableValue observable = ControlUtils.observeText(toObserve);
		observable.addValueChangeListener(this);
		manager.addObservable(observable);
	}

	public void doTest() {
		results.setInput(engine.test(regExp.getText(), replacement.getText(),
				input.getInputs(), RegexpUIPlugin.getDefault().getFlags()));
	}

	@Override
	public void dispose() {
		super.dispose();

		manager.dispose();

		if (input != null) {
			input.dispose();
		}

		IPreferenceStore store = RegexpUIPlugin.getDefault()
				.getPreferenceStore();
		if (store != null) {
			store.removePropertyChangeListener(this);
		}
	}

	@Override
	public void setFocus() {
		regExp.setFocus();
	}
}
