package ac.soton.eventb.prover.internal.prefs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

import ac.soton.eventb.prover.plugin.ProverPlugIn;

/**
 * <p>A customised list field editor that manages a list of input values. 
 * The editor displays a list containing the values, buttons for
 * adding and removing values, and Up and Down buttons to adjust
 * the order of elements in the list.</p>
 * 
 * <p> This class is not intended to be used or subclassed by clients.</p>
 * 
 * @author maamria
 */
final class CustomisedListEditor extends FieldEditor {

	/**
     * The Add button.
     */
    private Button addButton;

    private int afterRemoveIndex = -1;

    /**
     * The button box containing the Add, Remove, Up, and Down buttons;
     * <code>null</code> if none (before creation or after disposal).
     */
    private Composite buttonBox;

    /**
     * The Down button.
     */
    private Button downButton;
    
    /**
     * The list widget; <code>null</code> if none
     * (before creation or after disposal).
     */
    private List list;

    private int nextCatIndex = 0;

    /**
     * The Remove button.
     */
    private Button removeButton;
    /**
     * The selection listener.
     */
    private SelectionListener selectionListener;
    
    /**
     * The Up button.
     */
    private Button upButton;
    
   

    /**
     * Creates a new list field editor 
     */
    protected CustomisedListEditor() {
    }

    /**
     * Creates a list field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    protected CustomisedListEditor(String name, String labelText, Composite parent) {
        init(name, labelText);
        createControl(parent);
    }

    /**
     * Creates a selection listener.
     */
    public void createSelectionListener() {
        selectionListener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                Widget widget = event.widget;
                if (widget == addButton) {
                    addPressed();
                } else if (widget == removeButton) {
                    removePressed();
                } else if (widget == upButton) {
                    upPressed();
                } else if (widget == downButton) {
                    downPressed();
                } else if (widget == list) {
                    selectionChanged();
                } 
            }
        };
    }

    /**
     * Returns this field editor's button box containing the Add, Remove,
     * Up, and Down button.
     *
     * @param parent the parent control
     * @return the button box
     */
    public Composite getButtonBoxControl(Composite parent) {
        if (buttonBox == null) {
            buttonBox = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            buttonBox.setLayout(layout);
            createButtons(buttonBox);
            buttonBox.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    addButton = null;
                    removeButton = null;
                    upButton = null;
                    downButton = null;
                    buttonBox = null;
                }
            });

        } else {
            checkParent(buttonBox, parent);
        }
        selectionChanged();
        return buttonBox;
    }

    /**
     * Returns this field editor's list control.
     *
     * @param parent the parent control
     * @return the list control
     */
    public List getListControl(Composite parent) {
        if (list == null) {
            list = new List(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL
                    | SWT.H_SCROLL);
            list.setFont(parent.getFont());
            list.addSelectionListener(getSelectionListener());
            list.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    list = null;
                }
            });
        } else {
            checkParent(list, parent);
        }
        return list;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public int getNumberOfControls() {
        return 2;
    }

    /**
	 * <p>Calculates a single string for the list of availabe categories. This is needed to store the 
	 * preference for categories.</p>
	 * @return a single string of categories delimited by ",".
	 */
	public String getStringValue(){
		return 
			TheoryPrefsUtils.toSingleString(list.getItems(),
				ProverPlugIn.CATEGORIES_DELIM);
	}

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        getListControl(parent).setEnabled(enabled);
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public void setFocus() {
        if (list != null) {
            list.setFocus();
        }
    }

    /**
	 * <p>Sets the categories list to the specified <code>strList</code>.</p>
	 * @param strList the new value
	 */
	public void setStringValues(String[] strList){
		list.setItems(strList);
	}

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) list.getLayoutData()).horizontalSpan = numColumns - 1;
    }

    /**
     * Combines the given list of items into a single string.
     * This method is the converse of <code>parseString</code>. 
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param items the list of items
     * @return the combined string
     * @see #parseString
     */
    protected String createList(String[] items) {
		return TheoryPrefsUtils.toSingleString(items,
				ProverPlugIn.CATEGORIES_DELIM);
	}

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        list = getListControl(parent);
        gd = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
        gd.horizontalSpan = numColumns - 1;
        gd.verticalSpan = 3;
        list.setLayoutData(gd);

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        buttonBox.setLayoutData(gd);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoad() {
        if (list != null) {
            String s = getPreferenceStore().getString(getPreferenceName());
            String[] array = parseString(s);
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
            selectionChanged();
        }
    }
    
    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoadDefault() {
        if (list != null) {
            list.removeAll();
            String s = getPreferenceStore().getDefaultString(
                    getPreferenceName());
            String[] array = parseString(s);
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
            selectionChanged();
        }
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doStore() {
        String s = createList(list.getItems());
        if (s != null) {
			getPreferenceStore().setValue(getPreferenceName(), s);
		}
    }

    /**
	 * The subclasses must override this to return the modified entry.
	 * 
	 * @param original
	 *            the new entry
	 * @return the modified entry. Return null to prevent modification.
	 */
	protected String getNewCategory() {
		InputDialog entryDialog = new InputDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "Add category",
				"Add category:", getNewInputObject(), null);
		if (entryDialog.open() == InputDialog.OK) {
			if(TheoryPrefsUtils.contains(list.getItems(),
					entryDialog.getValue())){
				return null;
			}
			return entryDialog.getValue();
		}
		return null;
	}

    /**
     * Creates and returns a new item for the list.
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @return a new item
     */
    protected String getNewInputObject() {
		return "newCategory"+nextCatIndex++;
	}

    /**
     * Returns this field editor's shell.
     * <p>
     * This method is internal to the framework; subclassers should not call
     * this method.
     * </p>
     *
     * @return the shell
     */
    protected Shell getShell() {
        if (addButton == null) {
			return null;
		}
        return addButton.getShell();
    }

    /**
     * Splits the given string into a list of strings.
     * This method is the converse of <code>createList</code>. 
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param stringList the string
     * @return an array of <code>String</code>
     * @see #createList
     */
    protected String[] parseString(String stringList) {
		return TheoryPrefsUtils.getAvailableCategories(stringList,
				ProverPlugIn.CATEGORIES_DELIM);
	}

    /**
     * Notifies that the Add button has been pressed.
     */
    private void addPressed() {
        setPresentsDefaultValue(false);
        doAdd();
    }

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box the box for the buttons
     */
    private void createButtons(Composite box) {
        addButton = createPushButton(box, "ListEditor.add");//$NON-NLS-1$
        removeButton = createPushButton(box, "ListEditor.remove");//$NON-NLS-1$
        upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
        downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
    }

    /**
     * Helper method to create a push button.
     * 
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
     * @return Button
     */
    private Button createPushButton(Composite parent, String key) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        int widthHint = convertHorizontalDLUsToPixels(button,
                IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(getSelectionListener());
        return button;
    }

    private void doAdd(){
		String modified = getNewCategory();
		if (modified != null) {
			int selectedIndex = list
					.getSelectionIndex()+1;
			list.add(modified, selectedIndex);
			list.select(selectedIndex);
			selectionChanged();
		}
	}

    /**
     * Notifies that the Down button has been pressed.
     */
    private void downPressed() {
        swap(false);
    }

    /**
     * Returns this field editor's selection listener.
     * The listener is created if nessessary.
     *
     * @return the selection listener
     */
    private SelectionListener getSelectionListener() {
        if (selectionListener == null) {
			createSelectionListener();
		}
        return selectionListener;
    }

    
    /**
     * Notifies that the Remove button has been pressed.
     */
    private void removePressed() {
        setPresentsDefaultValue(false);
        int index = list.getSelectionIndex();
        if (index >= 0) {
            list.remove(index);
            if(list.getItemCount() > 0){
            	if(index > 0)
            		afterRemoveIndex = index-1;
            	else 
            		afterRemoveIndex = 0;
            }
            selectionChanged();
        }
    }
	/**
     * Notifies that the list selection has changed.
     */
    private void selectionChanged() {

        int index = list.getSelectionIndex();
        int size = list.getItemCount();
        // to disable certain buttons when the default category is selected
        String sel = null;
        boolean isDefSelected = false;
        if(index != -1 ){
        	sel = list.getItem(index);
            isDefSelected = sel.equals(ProverPlugIn.THEORY_MAIN_CAT);
        }
        
        if(sel != null){
        	removeButton.setEnabled(index >= 0 && !isDefSelected);
        	
        }
        else {
        	removeButton.setEnabled(index >= 0);
        	
        }
        upButton.setEnabled(size > 1 && index > 0);
        downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
      
        // if nothing is selected, select something
        if(afterRemoveIndex != -1){
        	list.select(afterRemoveIndex);
        	afterRemoveIndex = -1;
        	selectionChanged();
        	// to ensure visibility
        	list.redraw();
        }
        else if(index == -1 && size > 0){
        	list.select(0);
        	selectionChanged();
        	// to ensure visibility
        	list.redraw();
        }
    }
	
	/**
     * Moves the currently selected item up or down.
     *
     * @param up <code>true</code> if the item should move up,
     *  and <code>false</code> if it should move down
     */
    private void swap(boolean up) {
        setPresentsDefaultValue(false);
        int index = list.getSelectionIndex();
        int target = up ? index - 1 : index + 1;

        if (index >= 0) {
            String[] selection = list.getSelection();
            Assert.isTrue(selection.length == 1);
            list.remove(index);
            list.add(selection[0], target);
            list.setSelection(target);
        }
        selectionChanged();
    }
	
	/**
     * Notifies that the Up button has been pressed.
     */
    private void upPressed() {
        swap(true);
    }
}
