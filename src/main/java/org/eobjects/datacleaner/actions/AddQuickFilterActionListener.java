package org.eobjects.datacleaner.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.eobjects.analyzer.configuration.AnalyzerBeansConfiguration;
import org.eobjects.analyzer.data.DataTypeFamily;
import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.descriptors.ConfiguredPropertyDescriptor;
import org.eobjects.analyzer.descriptors.FilterBeanDescriptor;
import org.eobjects.analyzer.job.builder.AnalysisJobBuilder;
import org.eobjects.analyzer.job.builder.FilterJobBuilder;
import org.eobjects.analyzer.util.CollectionUtils;
import org.eobjects.datacleaner.util.DisplayNameComparator;
import org.eobjects.datacleaner.widgets.tooltip.DescriptorMenuItem;

public class AddQuickFilterActionListener implements ActionListener {

	private final JButton _button;
	private final AnalyzerBeansConfiguration _configuration;
	private final AnalysisJobBuilder _analysisJobBuilder;
	private final InputColumn<?> _inputColumn;

	public AddQuickFilterActionListener(JButton button, AnalyzerBeansConfiguration configuration,
			AnalysisJobBuilder analysisJobBuilder, InputColumn<?> inputColumn) {
		_button = button;
		_configuration = configuration;
		_analysisJobBuilder = analysisJobBuilder;
		_inputColumn = inputColumn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JPopupMenu popup = new JPopupMenu("Quick filter");
		Collection<FilterBeanDescriptor<?, ?>> descriptors = _configuration.getDescriptorProvider()
				.getFilterBeanDescriptors();
		descriptors = CollectionUtils.sorted(descriptors, new DisplayNameComparator());

		for (FilterBeanDescriptor<?, ?> descriptor : descriptors) {
			Set<ConfiguredPropertyDescriptor> inputs = descriptor.getConfiguredPropertiesForInput();
			if (inputs.size() == 1) {
				ConfiguredPropertyDescriptor input = inputs.iterator().next();
				if (!input.isArray()) {
					DataTypeFamily currentDataTypeFamily = _inputColumn.getDataTypeFamily();
					DataTypeFamily inputDataTypeFamily = input.getInputColumnDataTypeFamily();
					if (inputDataTypeFamily == DataTypeFamily.UNDEFINED || currentDataTypeFamily == inputDataTypeFamily) {
						JMenuItem menuItem = createMenuItem(descriptor);
						popup.add(menuItem);
					}
				}
			}
		}
		popup.show(_button, 0, _button.getHeight());
	}

	private JMenuItem createMenuItem(final FilterBeanDescriptor<?, ?> descriptor) {
		JMenuItem menuItem = new DescriptorMenuItem(descriptor);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FilterJobBuilder<?, ?> fjb = _analysisJobBuilder.addFilter(descriptor);
				fjb.addInputColumn(_inputColumn);
			}
		});
		return menuItem;
	}

}
