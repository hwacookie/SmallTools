package de.mbaaba.tool.pw.data;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class WorktimeContentProvider implements IStructuredContentProvider {

	private Object[] input;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		input = (Object[]) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return input;
	}

}
