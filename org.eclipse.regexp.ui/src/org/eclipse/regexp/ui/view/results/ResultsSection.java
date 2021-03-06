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
package org.eclipse.regexp.ui.view.results;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.regexp.RegexpResult;
import org.eclipse.regexp.RegexpResult.Group;
import org.eclipse.regexp.TestResult;
import org.eclipse.regexp.ui.common.ControlUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class ResultsSection {

	private static final String EMPTY = "<empty>";
	private static final String NONE = "<none>";

	private Section section;
	private FormToolkit toolkit;
	private Composite content;

	public void create(final Composite parent, final FormToolkit toolkit) {
		this.section = toolkit.createSection(parent, Section.TITLE_BAR
				| Section.TWISTIE);
		this.toolkit = toolkit;

		section.setText("Result");
		GridDataFactory.fillDefaults().span(2, 1).applyTo(section);
		section.setExpanded(true);

		final Composite resultPanel = toolkit
				.createComposite(section, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(resultPanel);
		section.setClient(resultPanel);

		setInput(null);
	}

	public void setInput(final TestResult testReult) {
		boolean visible = testReult != null
				&& !testReult.getInputResults().isEmpty();
		setContentVisible(visible);
		if (visible) {
			for (RegexpResult result : testReult.getInputResults()) {
				Section resultSection = toolkit
						.createSection(content, SWT.NONE);
				resultSection.setText("Input " + (result.index + 1));

				final Composite resultPanel = toolkit.createComposite(
						resultSection, SWT.NONE);
				GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 0)
						.spacing(5, 2).applyTo(resultPanel);
				resultSection.setClient(resultPanel);

				createText("Input:", String.valueOf(result.input), resultPanel);
				createText("Matches:", boolLabel(result.mathes), resultPanel);
				createText("Looking At:", boolLabel(result.lookingAt),
						resultPanel);
				createText("Found:", boolLabel(result.find), resultPanel);
				createText("Replace First:", result.replaceFirst, resultPanel);
				createText("Replace All:", result.replaceAll, resultPanel);
				createText("Groups:", groupsLabel(result), resultPanel);
			}
			section.setExpanded(true);
		}
		ControlUtils.refreshScroll(section);
	}

	private void setContentVisible(final boolean visible) {
		if (content != null) {
			GridData data = (GridData) content.getLayoutData();
			data.exclude = true;
			content.setVisible(false);
		}

		content = toolkit.createComposite((Composite) section.getClient());
		GridLayoutFactory.fillDefaults().spacing(0, 15).applyTo(content);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(content);

		GridData data = (GridData) section.getLayoutData();
		data.exclude = !visible;

		section.setVisible(visible);
	}

	private String boolLabel(final boolean val) {
		return val ? "Yes" : "No";
	}

	private String groupsLabel(final RegexpResult result) {
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < result.groups.size(); i++) {
			if (i > 0) {
				build.append("\n");
			}
			Group group = result.groups.get(i);
			build.append(String.format("%d: [%d,%d] %s", i, group.start,
					group.end, group.group));
		}
		return build.toString();
	}

	private void createText(final String label, final String value,
			final Composite panel) {
		GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP)
				.applyTo(toolkit.createLabel(panel, label));
		Text valueText = new Text(panel, SWT.NONE);
		if (value == null) {
			valueText.setText(NONE);
		} else if (value.isEmpty()) {
			valueText.setText(EMPTY);
		} else {
			valueText.setText(value);
		}
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.applyTo(valueText);
	}
}
