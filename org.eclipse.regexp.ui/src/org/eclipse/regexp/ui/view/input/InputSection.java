package org.eclipse.regexp.ui.view.input;

import java.util.List;

import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.regexp.ui.common.ControlUtils;
import org.eclipse.regexp.ui.common.SectionToolbar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class InputSection implements IValueChangeListener {

	private FormToolkit toolkit;
	private Section section;

	private final List<InputListener> listeners = Lists.newArrayList();
	private final List<InputControl> inputs = Lists.newArrayList();

	private final ObservablesManager manager = new ObservablesManager();

	public void create(final Composite parent, final FormToolkit toolkit) {
		this.section = toolkit.createSection(parent, Section.TITLE_BAR
				| Section.TWISTIE);
		this.toolkit = toolkit;
		section.setText("Input");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1)
				.applyTo(section);
		section.setExpanded(true);

		final Composite inputPanel = toolkit.createComposite(section, SWT.NONE);
		section.setClient(inputPanel);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(inputPanel);

		final SectionToolbar inputToolbar = new SectionToolbar(section);
		inputToolbar.add(new AddInputAction(this));
		inputToolbar.done();

		addInput();
	}

	public void addInput() {
		InputControl inputControl = new InputControl(this);
		inputControl.create((Composite) section.getClient(), toolkit,
				inputs.size() + 1);

		IObservableValue inputObservable = inputControl.observeInput();
		inputObservable.addValueChangeListener(this);
		manager.addObservable(inputObservable);

		inputs.add(inputControl);
		refresh();
		handleValueChange(null);
	}

	public void removeInput(final InputControl input) {
		inputs.remove(input);
		handleValueChange(null);
	}

	public class InputControlToStr implements Function<InputControl, String> {
		@Override
		public String apply(final InputControl ctrl) {
			return ctrl.getInput();
		}
	}

	public String[] getInputs() {
		return Collections2.transform(inputs, new InputControlToStr()).toArray(
				new String[0]);
	}

	public void addListener(final InputListener listener) {
		listeners.add(listener);
	}

	@Override
	public void handleValueChange(final ValueChangeEvent event) {
		for (InputListener listener : listeners) {
			listener.inputChanged();
		}
	}

	public void removeListeer(final InputListener listener) {
		listeners.remove(listener);
	}

	public void refresh() {
		for (int i = 0; i < inputs.size(); i++) {
			inputs.get(i).refresh(i + 1);
		}
		ControlUtils.refreshScroll((Composite) section.getClient());
	}

	public void dispose() {
		manager.dispose();
	}
}
