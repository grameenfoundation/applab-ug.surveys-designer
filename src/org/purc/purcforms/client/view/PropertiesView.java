package org.purc.purcforms.client.view;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.cmd.ChangedFieldCmd;
import org.purc.purcforms.client.controller.IFormActionListener;
import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Calculation;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.RepeatQtnsDef;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.DescTemplateWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;


/**
 * View responsible for displaying and hence allow editing of 
 * form, page, question, or question option properties.
 * 
 * @author daniel
 *
 */
public class PropertiesView extends Composite implements IFormSelectionListener,ItemSelectionListener{

	/** List box index for no selected data type. */
	private static final byte DT_INDEX_NONE = -1;

	/** List box index for text data type. */
	private static final byte DT_INDEX_TEXT = 0;

	/** List box index for number data type. */
	private static final byte DT_INDEX_NUMBER = 1;

	/** List box index for decimal data type. */
	private static final byte DT_INDEX_DECIMAL = 2;

	/** List box index for date data type. */
	private static final byte DT_INDEX_DATE = 3;

	/** List box index for time data type. */
	private static final byte DT_INDEX_TIME = 4;

	/** List box index for dateTime data type. */
	private static final byte DT_INDEX_DATE_TIME = 5;

	/** List box index for boolean data type. */
	//private static final byte DT_INDEX_BOOLEAN = 6;

	/** List box index for single select data type. */
	private static final byte DT_INDEX_SINGLE_SELECT = 6;

	/** List box index for multiple select data type. */
	private static final byte DT_INDEX_MULTIPLE_SELECT = 7;

	/** List box index for repeat data type. */
	private static final byte DT_INDEX_REPEAT = 8;

	/** List box index for image data type. */
	private static final byte DT_INDEX_IMAGE = 9;

	/** List box index for video data type. */
	private static final byte DT_INDEX_VIDEO = 10;

	/** List box index for audio data type. */
	private static final byte DT_INDEX_AUDIO = 11;

	/** List box index for single select dynamic data type. */
	//private static final byte DT_INDEX_SINGLE_SELECT_DYNAMIC = 13;

	/** List box index for gps data type. */
	private static final byte DT_INDEX_GPS = 12;

	/** List box index for barcode data type. */
	private static final byte DT_INDEX_BARCODE = 13;

	/** Table used for organising widgets in a table format. */
	private FlexTable table = new FlexTable();

	/** Widget for displaying the list of data types. */
	private ListBox cbDataType = new ListBox(false);

	/** Widget for setting the visibility property. */
	private CheckBox chkVisible = new CheckBox();

	/** Widget for setting the enabled property. */
	private CheckBox chkEnabled = new CheckBox();

	/** Widget for setting the locked property. */
	private CheckBox chkLocked = new CheckBox();

	/** Widget for setting the required property. */
	private CheckBox chkRequired = new CheckBox();

	/** Widget for setting the text property. */
	private TextBox txtText = new TextBox();

	/** Widget for setting the help text property. */
	private TextBox txtHelpText = new TextBox();

	/** Widget for setting the binding property. */
	private TextBox txtBinding = new TextBox();

	/** Widget for setting the default value property. */
	private TextBox txtDefaultValue = new TextBox();

	/** Widget for setting the description template property. */
	private TextBox txtDescTemplate = new TextBox();

	private Label lblDescTemplate;

	private TextBox txtCalculation = new TextBox();

	/** Widget for selecting fields which define the description template. */
	private DescTemplateWidget btnDescTemplate; // = new Button("Create/Edit");

	private DescTemplateWidget btnCalculation;

	/** Widget for setting the form key property. */
	private TextBox txtFormKey = new TextBox();

	/** The selected object which could be FormDef, PageDef, QuestionDef or OptionDef */
	private Object propertiesObj;

	/** Listener to form change events. */
	private IFormChangeListener formChangeListener;

	/** Widget for defining skip rules. */
	private SkipRulesView skipRulesView = new SkipRulesView(this);

	/** Widget for defining validation rules. */
	private ValidationRulesView validationRulesView = new ValidationRulesView(this);

	/** Widget for defining dynamic selection lists. */
	private DynamicListsView dynamicListsView = new DynamicListsView(this);

	/** Listener to form action events. */
	private IFormActionListener formActionListener;
	
	DecoratedTabPanel tabs;

	private String beforeChangeText;


	/**
	 * Creates a new instance of the properties view widget.
	 */
	public PropertiesView(){

		btnDescTemplate = new DescTemplateWidget(this);
		btnCalculation = new DescTemplateWidget(this);

		table.setWidget(0, 0, new Label(LocaleText.get("text")));
		table.setWidget(1, 0, new Label(LocaleText.get("helpText")));
		table.setWidget(2, 0, new Label(LocaleText.get("type")));
		table.setWidget(3, 0, new Label(LocaleText.get("binding")));
		table.setWidget(4, 0, new Label(LocaleText.get("visible")));
		table.setWidget(5, 0, new Label(LocaleText.get("enabled")));
		table.setWidget(6, 0, new Label(LocaleText.get("locked")));
		table.setWidget(7, 0, new Label(LocaleText.get("required")));
		table.setWidget(8, 0, new Label(LocaleText.get("defaultValue")));
		table.setWidget(9, 0, new Label(LocaleText.get("calculation")));

		lblDescTemplate = new Label(LocaleText.get("descriptionTemplate"));
		table.setWidget(10, 0, lblDescTemplate);
		table.setWidget(11, 0, new Label(LocaleText.get("formKey")));

		table.setWidget(0, 1, txtText);
		table.setWidget(1, 1, txtHelpText);
		table.setWidget(2, 1, cbDataType);
		table.setWidget(3, 1, txtBinding);
		table.setWidget(4, 1, chkVisible);
		table.setWidget(5, 1, chkEnabled);
		table.setWidget(6, 1, chkLocked);
		table.setWidget(7, 1, chkRequired);
		table.setWidget(8, 1, txtDefaultValue);

		HorizontalPanel panel = new HorizontalPanel();
		panel.add(txtCalculation);
		panel.add(btnCalculation);
		panel.setCellWidth(btnCalculation, "20%");
		FormUtil.maximizeWidget(txtCalculation);
		FormUtil.maximizeWidget(panel);
		table.setWidget(9, 1, panel);

		panel = new HorizontalPanel();
		panel.add(txtDescTemplate);
		panel.add(btnDescTemplate);
		panel.setCellWidth(btnDescTemplate, "20%");
		FormUtil.maximizeWidget(txtDescTemplate);
		FormUtil.maximizeWidget(panel);
		table.setWidget(10, 1, panel);

		table.setWidget(11, 1, txtFormKey);

		table.setStyleName("cw-FlexTable");

		cbDataType.addItem(LocaleText.get("qtnTypeText"));
		cbDataType.addItem(LocaleText.get("qtnTypeNumber"));
		cbDataType.addItem(LocaleText.get("qtnTypeDecimal"));
		cbDataType.addItem(LocaleText.get("qtnTypeDate"));
		cbDataType.addItem(LocaleText.get("qtnTypeTime"));
		cbDataType.addItem(LocaleText.get("qtnTypeDateTime"));
		//cbDataType.addItem(LocaleText.get("qtnTypeBoolean"));
		cbDataType.addItem(LocaleText.get("qtnTypeSingleSelect"));
		cbDataType.addItem(LocaleText.get("qtnTypeMultSelect"));
		cbDataType.addItem(LocaleText.get("qtnTypeRepeat"));
		cbDataType.addItem(LocaleText.get("qtnTypePicture"));
		cbDataType.addItem(LocaleText.get("qtnTypeVideo"));
		cbDataType.addItem(LocaleText.get("qtnTypeAudio"));
		//cbDataType.addItem(LocaleText.get("qtnTypeSingleSelectDynamic"));
		cbDataType.addItem(LocaleText.get("qtnTypeGPS"));
		cbDataType.addItem(LocaleText.get("qtnTypeBarcode"));

		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setHorizontalAlignment(15, 1, HasHorizontalAlignment.ALIGN_CENTER);

		table.setWidth("100%");
		cellFormatter.setWidth(0, 0, "20%");
		//cellFormatter.setColSpan(0, 0, 2);

		//cellFormatter.setWidth(9, 0, "20"+PurcConstants.UNITS);
		//cellFormatter.setWidth(9, 1, "20"+PurcConstants.UNITS);

		txtText.setWidth("100%");
		txtHelpText.setWidth("100%");
		txtBinding.setWidth("100%");
		txtDefaultValue.setWidth("100%");
		cbDataType.setWidth("100%");
		txtFormKey.setWidth("100%");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSpacing(5);
		verticalPanel.add(table);

		tabs = new DecoratedTabPanel();
		tabs.add(skipRulesView, LocaleText.get("skipLogic"));
		tabs.add(validationRulesView, LocaleText.get("validationLogic"));
		tabs.add(dynamicListsView, LocaleText.get("dynamicLists"));

		tabs.selectTab(0);
		verticalPanel.add(tabs);
		FormUtil.maximizeWidget(tabs);

		FormUtil.maximizeWidget(verticalPanel);
		initWidget(verticalPanel);

		setupEventListeners();

		cbDataType.setSelectedIndex(-1);

		enableQuestionOnlyProperties(false);
		txtText.setEnabled(false);
		//txtDescTemplate.setVisible(false);
		//btnDescTemplate.setVisible(false);
		enableDescriptionTemplate(false);
		txtCalculation.setEnabled(false);
		btnCalculation.setEnabled(false);
		txtBinding.setEnabled(false);

		txtText.setTitle(LocaleText.get("questionTextDesc"));
		txtHelpText.setTitle(LocaleText.get("questionDescDesc"));
		txtBinding.setTitle(LocaleText.get("questionIdDesc"));
		txtDefaultValue.setTitle(LocaleText.get("defaultValDesc"));
		cbDataType.setTitle(LocaleText.get("questionTypeDesc"));

		DOM.sinkEvents(getElement(), Event.ONKEYDOWN | DOM.getEventsSunk(getElement()));
	}

	/**
	 * Sets up event listeners.
	 */
	private void setupEventListeners(){
		//Check boxes.k
		chkVisible.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				boolean visible = chkVisible.getValue();
				((QuestionDef)propertiesObj).setVisible(visible);
				formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_VISIBLE, String.valueOf(!visible), true);
			}
		});

		chkEnabled.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				boolean enabled = chkEnabled.getValue();
				((QuestionDef)propertiesObj).setEnabled(enabled);
				formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_ENABLED, String.valueOf(!enabled), true);
			}
		});

		chkLocked.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				boolean locked = chkLocked.getValue();
				((QuestionDef)propertiesObj).setLocked(locked);
				formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_LOCKED, String.valueOf(!locked), true);
			}
		});

		chkRequired.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				boolean required = chkRequired.getValue();
				((QuestionDef)propertiesObj).setRequired(required);
				formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_REQUIRED, String.valueOf(!required), true);
			}
		});

		//Text boxes.
		txtDefaultValue.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				updateDefaultValue(true);
				beforeChangeText = null;
			}
		});
		txtDefaultValue.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {

				if(beforeChangeText == null){
					beforeChangeText = ((QuestionDef)propertiesObj).getDefaultValue();

					if(beforeChangeText == null)
						beforeChangeText = "";
				}

				updateDefaultValue(false);
			}
		});

		txtHelpText.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				updateHelpText(true);

				beforeChangeText = null;
			}
		});
		txtHelpText.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {

				if(beforeChangeText == null){
					beforeChangeText = ((QuestionDef)propertiesObj).getHelpText();

					if(beforeChangeText == null)
						beforeChangeText = "";
				}

				updateHelpText(false);
			}
		});

		txtHelpText.addKeyDownHandler(new KeyDownHandler(){
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				if(keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_DOWN)
					cbDataType.setFocus(true);
				else if(keyCode == KeyCodes.KEY_UP){
					txtText.setFocus(true);
					txtText.selectAll();
				}
			}
		});

		txtBinding.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				updateBinding(true);

				beforeChangeText = null;
			}
		});
		txtBinding.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {

				if(beforeChangeText == null){
					
					if(propertiesObj instanceof FormDef)
						beforeChangeText = ((FormDef)propertiesObj).getBinding();
					else if(propertiesObj instanceof QuestionDef)
						beforeChangeText = ((QuestionDef)propertiesObj).getBinding();
					else if(propertiesObj instanceof OptionDef)
						beforeChangeText = ((OptionDef)propertiesObj).getBinding();

					if(beforeChangeText == null)
						beforeChangeText = "";
				}

				String s = txtBinding.getText();

				s = s.replace("%", "");
				s = s.replace("(", "");
				s = s.replace("!", "");
				s = s.replace("&", "");
				//s = s.replace(".", ""); //Looks like this is an allowed character in xml node names.
				s = s.replace("'", "");
				s = s.replace("\"", "");
				s = s.replace("$", "");
				s = s.replace("#", "");

				txtBinding.setText(s);
				updateBinding(false);
			}
		});

		txtBinding.addKeyDownHandler(new KeyDownHandler(){
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_UP){
					if(cbDataType.isEnabled())
						cbDataType.setFocus(true);
					else{
						txtText.setFocus(true);
						txtText.selectAll();
					}
				}
			}
		});

		txtBinding.addKeyPressHandler(new KeyPressHandler(){
			public void onKeyPress(KeyPressEvent event) {
				if(propertiesObj instanceof PageDef){
					if(!Character.isDigit(event.getCharCode())){
						((TextBox) event.getSource()).cancelKey(); 
						return;
					}
				}
				else if(propertiesObj instanceof FormDef || propertiesObj instanceof QuestionDef){
					if(((TextBox) event.getSource()).getCursorPos() == 0){
						if(!isAllowedXmlNodeNameStartChar(event.getCharCode())){
							((TextBox) event.getSource()).cancelKey(); 
							return;
						}
					}
					else if(!isAllowedXmlNodeNameChar(event.getCharCode())){
						((TextBox) event.getSource()).cancelKey(); 
						return;
					}
				} //else OptionDef varname can be anything
			}
		});

		txtText.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				String orgText = getSelObjetOriginalText();
				updateText(true);
				updateSelObjBinding(orgText);

				beforeChangeText = null;
			}
		});
		txtText.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {
				String orgText = getSelObjetOriginalText();

				if(beforeChangeText == null){
					
					if(propertiesObj instanceof PageDef)
						orgText = ((PageDef)propertiesObj).getName();
					
					beforeChangeText = orgText;
					
					if(beforeChangeText == null)
						beforeChangeText = "";
				}

				updateText(false);
				updateSelObjBinding(orgText);
			}
		});

		txtText.addKeyDownHandler(new KeyDownHandler(){
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER || event.getNativeKeyCode() == KeyCodes.KEY_DOWN){
					if(txtHelpText.isEnabled())
						txtHelpText.setFocus(true);
					else{
						txtBinding.setFocus(true);
						txtBinding.selectAll();
					}
				}
			}
		});

		txtDescTemplate.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				updateDescTemplate(true);

				beforeChangeText = null;
			}
		});
		txtDescTemplate.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {

				if(beforeChangeText == null){
					beforeChangeText = ((FormDef)propertiesObj).getDescriptionTemplate();
					
					if(beforeChangeText == null)
						beforeChangeText = "";
				}

				updateDescTemplate(false);
			}
		});

		txtCalculation.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				updateCalculation(true);

				beforeChangeText = null;
			}
		});
		txtCalculation.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {
				updateCalculation(false);
			}
		});

		//Combo boxes
		cbDataType.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				updateDataType();
			}
		});
		cbDataType.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				updateDataType();
			}
		});
		cbDataType.addKeyDownHandler(new KeyDownHandler(){
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeEvent().getKeyCode();
				if(keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_DOWN){
					txtBinding.setFocus(true);
					txtBinding.selectAll();
				}
				else if(keyCode == KeyCodes.KEY_UP){
					txtHelpText.setFocus(true);
					txtHelpText.selectAll();
				}
			}
		});

		txtFormKey.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				updateFormKey(true);

				beforeChangeText = null;
			}
		});
		txtFormKey.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {

				if(beforeChangeText == null){
					beforeChangeText = ((FormDef)propertiesObj).getFormKey();
					
					if(beforeChangeText == null)
						beforeChangeText = "";
				}

				updateFormKey(false);
			}
		});
	}

	private String getSelObjetOriginalText(){
		if(propertiesObj instanceof FormDef)
			return ((FormDef)propertiesObj).getName();
		else if(propertiesObj instanceof QuestionDef)
			return ((QuestionDef)propertiesObj).getText();
		else if(propertiesObj instanceof OptionDef )
			return ((OptionDef)propertiesObj).getText();
		
		return null;
	}

	private void updateSelObjBinding(String orgText){

		if(orgText == null)
			return;

		String orgTextDefBinding = FormDesignerUtil.getXmlTagName(getTextWithoutDecTemplate(orgText));

		if(propertiesObj != null && Context.allowBindEdit() && !Context.isStructureReadOnly()){
			String text = getTextWithoutDecTemplate(txtText.getText().trim());
			String name = FormDesignerUtil.getXmlTagName(text);
			if(propertiesObj instanceof FormDef && ((FormDef)propertiesObj).getBinding().equals(orgTextDefBinding)){
				((FormDef)propertiesObj).setBinding(name);
				txtBinding.setText(name);
			}
			else if(propertiesObj instanceof QuestionDef && ((QuestionDef)propertiesObj).getBinding().equals(orgTextDefBinding) /*startsWith("question")*/){
				((QuestionDef)propertiesObj).setBinding(name);
				txtBinding.setText(name);
			}
			else if(propertiesObj instanceof OptionDef && ((OptionDef)propertiesObj).getBinding().equals(orgTextDefBinding) /*.startsWith("option")*/){
				((OptionDef)propertiesObj).setBinding(name);
				txtBinding.setText(name);
			}
		}
	}


	/**
	 * Gets text without the description template, for a given text.
	 * 
	 * @param text the text to parse.
	 * @return the text without the description template.
	 */
	public static String getTextWithoutDecTemplate(String text){
		if(text.contains("${")){
			if(text.indexOf("}$") < text.length() - 2)
				text = text.substring(0,text.indexOf("${")) + text.substring(text.indexOf("}$") + 2);
			else
				text = text.substring(0,text.indexOf("${"));
		}
		return text;
	}


	/**
	 * Checks if a given character is allowed to begin an xml node name.
	 * 
	 * @param keyCode the character code.
	 * @return true if is allowed, else false.
	 */
	private boolean isAllowedXmlNodeNameStartChar(char keyCode){
		return ((keyCode >= 'a' && keyCode <= 'z') || (keyCode >= 'A' && keyCode <= 'Z') || FormUtil.isControlChar(keyCode));
	}

	/**
	 * Checks if a character is allowed in an xml node name.
	 * 
	 * @param keyCode the character code.
	 * @return true if allowed, else false.
	 */
	private boolean isAllowedXmlNodeNameChar(char keyCode){
		return isAllowedXmlNodeNameStartChar(keyCode) || Character.isDigit(keyCode) || keyCode == '-' || keyCode == '_' || keyCode == '.';
	}

	/**
	 * Updates the selected object with the new text as typed by the user.
	 */
	private void updateText(boolean changeComplete){
		if(propertiesObj == null)
			return;

		if(propertiesObj instanceof QuestionDef)
			((QuestionDef)propertiesObj).setText(txtText.getText());
		else if(propertiesObj instanceof OptionDef)
			((OptionDef)propertiesObj).setText(txtText.getText());
		else if(propertiesObj instanceof PageDef)
			((PageDef)propertiesObj).setName(txtText.getText());
		else if(propertiesObj instanceof FormDef)
			((FormDef)propertiesObj).setName(txtText.getText());

		formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_TEXT, beforeChangeText, changeComplete);
	}


	private void updateFormKey(boolean changeComplete){
		if(propertiesObj == null)
			return;

		if(propertiesObj instanceof FormDef)
			((FormDef)propertiesObj).setFormKey(txtFormKey.getText());

		formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_FORM_KEY, beforeChangeText, changeComplete);
	}


	/**
	 * Updates the selected object with the new description template as typed by the user.
	 */
	private void updateDescTemplate(boolean changeComplete){
		if(propertiesObj == null)
			return;

		else if(propertiesObj instanceof FormDef){
			((FormDef)propertiesObj).setDescriptionTemplate(txtDescTemplate.getText());
			formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_DESCRIPTION_TEMPLATE, beforeChangeText, changeComplete);
		}
	}


	private void updateCalculation(boolean changeComplete){
		if(propertiesObj == null)
			return;

		assert(propertiesObj instanceof QuestionDef);
		Context.getFormDef().updateCalculation((QuestionDef)propertiesObj, txtCalculation.getText());
	}


	/**
	 * Updates the selected object with the new binding as typed by the user.
	 */
	private void updateBinding(boolean changeComplete){
		if(propertiesObj == null)
			return;

		if(txtBinding.getText().trim().length() == 0)
			return;

		if(propertiesObj instanceof QuestionDef)
			((QuestionDef)propertiesObj).setBinding(txtBinding.getText());
		else if(propertiesObj instanceof OptionDef)
			((OptionDef)propertiesObj).setBinding(txtBinding.getText());
		else if(propertiesObj instanceof FormDef)
			((FormDef)propertiesObj).setBinding(txtBinding.getText());
		else if(propertiesObj instanceof PageDef){
			try{
				((PageDef)propertiesObj).setPageNo(Integer.parseInt(txtBinding.getText()));
			}catch(Exception ex){
				return;
			}
		}

		formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_BINDING, beforeChangeText, changeComplete);
	}

	/**
	 * Updates the selected object with the new help text as typed by the user.
	 */
	private void updateHelpText(boolean changeComplete){
		if(propertiesObj == null)
			return;

		((QuestionDef)propertiesObj).setHelpText(txtHelpText.getText());
		formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_HELP_TEXT, beforeChangeText, changeComplete);
	}

	/**
	 * Updates the selected object with the new default value as typed by the user.
	 */
	private void updateDefaultValue(boolean changeComplete){
		if(propertiesObj == null)
			return;

		((QuestionDef)propertiesObj).setDefaultValue(txtDefaultValue.getText());
		formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_DEFAULT_VALUE, beforeChangeText, changeComplete);
	}

	/**
	 * Updates the selected object with the new data type as typed by the user.
	 */
	private void updateDataType(){
		if(propertiesObj == null)
			return;
		
		QuestionDef questionDef = (QuestionDef)propertiesObj;
		
		int dataType = getSelectedDataType();
		if(questionDef.getDataType() == dataType)
			return;

		boolean deleteKids = false;
		int index = cbDataType.getSelectedIndex();
		if((questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) &&
				!(index == DT_INDEX_SINGLE_SELECT || index == DT_INDEX_MULTIPLE_SELECT)){
			if(questionDef.getOptionCount() > 0 && !Window.confirm(LocaleText.get("changeWidgetTypePrompt"))){
				index = (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE) ? DT_INDEX_SINGLE_SELECT : DT_INDEX_MULTIPLE_SELECT;
				cbDataType.setSelectedIndex(index);
				return;
			}
			deleteKids = true;
		}
		else if((questionDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT) &&
				!(index == DT_INDEX_REPEAT)){
			if(!Window.confirm(LocaleText.get("changeWidgetTypePrompt"))){
				index = DT_INDEX_REPEAT;
				cbDataType.setSelectedIndex(index);
				return;
			}
			deleteKids = true;
		}

		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN)
			deleteKids = true;
		
		//cbDataType.setSelectedIndex(index);
		String oldValue = String.valueOf(questionDef.getDataType());
		setQuestionDataType(questionDef, dataType);
		formChangeListener.onFormItemChanged(propertiesObj, ChangedFieldCmd.PROPERTY_TYPE, oldValue, true);

		if(deleteKids)
			formChangeListener.onDeleteChildren(propertiesObj);
		
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
			selectDynamicListsTab();
	}
	
	
	private int getSelectedDataType(){
		int dataType = QuestionDef.QTN_TYPE_TEXT;

		switch(cbDataType.getSelectedIndex()){
		case DT_INDEX_NUMBER:
			dataType = QuestionDef.QTN_TYPE_NUMERIC;
			break;
		case DT_INDEX_DECIMAL:
			dataType = QuestionDef.QTN_TYPE_DECIMAL;
			break;
		case DT_INDEX_DATE:
			dataType = QuestionDef.QTN_TYPE_DATE;
			break;
		case DT_INDEX_TIME:
			dataType = QuestionDef.QTN_TYPE_TIME;
			break;
		case DT_INDEX_DATE_TIME:
			dataType = QuestionDef.QTN_TYPE_DATE_TIME;
			break;
		/*case DT_INDEX_BOOLEAN:
			dataType = QuestionDef.QTN_TYPE_BOOLEAN;
			break;*/
		case DT_INDEX_SINGLE_SELECT:
			dataType = QuestionDef.QTN_TYPE_LIST_EXCLUSIVE;
			break;
		case DT_INDEX_MULTIPLE_SELECT:
			dataType = QuestionDef.QTN_TYPE_LIST_MULTIPLE;
			break;
		case DT_INDEX_REPEAT:
			dataType = QuestionDef.QTN_TYPE_REPEAT;
			break;
		case DT_INDEX_IMAGE:
			dataType = QuestionDef.QTN_TYPE_IMAGE;
			break;
		case DT_INDEX_VIDEO:
			dataType = QuestionDef.QTN_TYPE_VIDEO;
			break;
		case DT_INDEX_AUDIO:
			dataType = QuestionDef.QTN_TYPE_AUDIO;
			break;
		/*case DT_INDEX_SINGLE_SELECT_DYNAMIC:
			dataType = QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC;
			break;*/
		case DT_INDEX_GPS:
			dataType = QuestionDef.QTN_TYPE_GPS;
			break;
		case DT_INDEX_BARCODE:
			dataType = QuestionDef.QTN_TYPE_BARCODE;
			break;
		}
		
		return dataType;
	}

	/**
	 * Sets the data type of a question definition object basing on selection
	 * in the type selection list box widget.
	 * 
	 * @param questionDef the question definition object.
	 * @param dataType the data type to set.
	 */
	private void setQuestionDataType(QuestionDef questionDef, int dataType){
	
		if(dataType == QuestionDef.QTN_TYPE_REPEAT && 
				questionDef.getDataType() != QuestionDef.QTN_TYPE_REPEAT)
			questionDef.setRepeatQtnsDef(new RepeatQtnsDef(questionDef));

		questionDef.setDataType(dataType);

		if(questionDef.getDataType() != QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
			dynamicListsView.setEnabled(false);
		else if(!dynamicListsView.isEnabled())
			dynamicListsView.setQuestionDef(questionDef);
	}

	/**
	 * Sets the listener for form change events.
	 * 
	 * @param formChangeListener the listener.
	 */
	public void setFormChangeListener(IFormChangeListener formChangeListener){
		this.formChangeListener = formChangeListener;
		skipRulesView.setFormChangeListener(formChangeListener);
		validationRulesView.setFormChangeListener(formChangeListener);
		dynamicListsView.setFormChangeListener(formChangeListener);
	}

	/**
	 * Sets values for widgets which deal with form definition properties.
	 * 
	 * @param formDef the form definition object.
	 */
	private void setFormProperties(FormDef formDef){
		enableQuestionOnlyProperties(false);

		txtText.setEnabled(true);
		//txtDescTemplate.setVisible(Context.isStructureReadOnly() ? false : true);
		//btnDescTemplate.setVisible(Context.isStructureReadOnly() ? false : true);
		enableDescriptionTemplate(Context.isStructureReadOnly() ? false : true);

		txtText.setText(formDef.getName());
		txtBinding.setText(formDef.getBinding());
		txtFormKey.setText(formDef.getFormKey());
		//skipRulesView.setFormDef(formDef);

		txtDescTemplate.setText(formDef.getDescriptionTemplate());
		btnDescTemplate.setFormDef(formDef);

		btnCalculation.setFormDef(formDef);
	}

	/**
	 * Sets values for widgets which deal with page definition properties.
	 * 
	 * @param pageDef the page definition object.
	 */
	private void setPageProperties(PageDef pageDef){
		enableQuestionOnlyProperties(false);

		txtText.setEnabled(true);
		//txtDescTemplate.setVisible(false);
		//btnDescTemplate.setVisible(false);
		enableDescriptionTemplate(false);
		txtCalculation.setEnabled(false);
		btnCalculation.setEnabled(false);

		txtText.setText(pageDef.getName());
		txtBinding.setText(String.valueOf(pageDef.getPageNo()));
		//skipRulesView.updateSkipRule();
	}

	/**
	 * Sets values for widgets which deal with question definition properties.
	 * 
	 * @param questionDef the question definition object.
	 */
	private void setQuestionProperties(QuestionDef questionDef){
		enableQuestionOnlyProperties(true);

		//txtDescTemplate.setVisible(false);
		enableDescriptionTemplate(false);

		txtText.setText(questionDef.getText());
		txtBinding.setText(questionDef.getBinding());
		txtHelpText.setText(questionDef.getHelpText());
		txtDefaultValue.setText(questionDef.getDefaultValue());

		chkVisible.setValue(questionDef.isVisible());
		chkEnabled.setValue(questionDef.isEnabled());
		chkLocked.setValue(questionDef.isLocked());
		chkRequired.setValue(questionDef.isRequired());

		setDataType(questionDef.getDataType());

		String calculationExpression = null;
		Calculation calculation = Context.getFormDef().getCalculation(questionDef);
		if(calculation != null)
			calculationExpression = calculation.getCalculateExpression();
		txtCalculation.setText(calculationExpression);

		//Skip logic processing is a bit slow and hence we wanna update the 
		//UI with the rest of simple quick properties as we process skip logic
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				skipRulesView.setQuestionDef((QuestionDef)propertiesObj);
				validationRulesView.setQuestionDef((QuestionDef)propertiesObj);
				dynamicListsView.setQuestionDef((QuestionDef)propertiesObj);
			}
		});
	}

	/**
	 * Sets values for widgets which deal with question option definition properties.
	 * 
	 * @param optionDef the option definition object.
	 */
	private void setQuestionOptionProperties(OptionDef optionDef){
		enableQuestionOnlyProperties(false);
		//txtDescTemplate.setVisible(false);
		//btnDescTemplate.setVisible(false);
		enableDescriptionTemplate(false);
		txtCalculation.setEnabled(false);
		btnCalculation.setEnabled(false);

		txtText.setText(optionDef.getText());
		txtBinding.setText(optionDef.getBinding());
		//skipRulesView.updateSkipRule();
	}

	/**
	 * Sets whether to enable question property widgets.
	 * 
	 * @param enabled true to enable them, false to disable them.
	 */
	private void enableQuestionOnlyProperties(boolean enabled){
		//boolean enable = (enabled && !Context.isStructureReadOnly()) ? true : false;
		boolean enable2 = (enabled && !Context.inLocalizationMode()) ? true : false;

		cbDataType.setEnabled(enable2);
		chkVisible.setEnabled(enable2);
		chkEnabled.setEnabled(enable2);
		chkLocked.setEnabled(enable2);
		chkRequired.setEnabled(enable2);
		txtDefaultValue.setEnabled(enable2);
		txtHelpText.setEnabled(enabled); //We allow localisation of help text.
		skipRulesView.setEnabled(enable2);
		validationRulesView.setEnabled(enable2);
		dynamicListsView.setEnabled(enable2);

		//btnDescTemplate.setVisible(enable2);
		txtCalculation.setEnabled(enable2);
		btnCalculation.setEnabled(enable2);

		clearProperties();
	}

	/**
	 * Selects the current question's data type in the data types drop down listbox.
	 * 
	 * @param type the current question's data type.
	 */
	private void setDataType(int type){
		int index = DT_INDEX_NONE;

		switch(type){
		case QuestionDef.QTN_TYPE_DATE:
			index = DT_INDEX_DATE;
			break;
		/*case QuestionDef.QTN_TYPE_BOOLEAN:
			index = DT_INDEX_BOOLEAN;
			break;*/
		case QuestionDef.QTN_TYPE_DATE_TIME:
			index = DT_INDEX_DATE_TIME;
			break;
		case QuestionDef.QTN_TYPE_DECIMAL:
			index = DT_INDEX_DECIMAL;
			break;
		case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE:
			index = DT_INDEX_SINGLE_SELECT;
			break;
		case QuestionDef.QTN_TYPE_LIST_MULTIPLE:
			index = DT_INDEX_MULTIPLE_SELECT;
			break;
		case QuestionDef.QTN_TYPE_NUMERIC:
			index = DT_INDEX_NUMBER;
			break;
		case QuestionDef.QTN_TYPE_REPEAT:
			index = DT_INDEX_REPEAT;
			break;
		case QuestionDef.QTN_TYPE_TEXT:
			index = DT_INDEX_TEXT;
			break;
		case QuestionDef.QTN_TYPE_TIME:
			index = DT_INDEX_TIME;
			break;
		case QuestionDef.QTN_TYPE_IMAGE:
			index = DT_INDEX_IMAGE;
			break;
		case QuestionDef.QTN_TYPE_VIDEO:
			index = DT_INDEX_VIDEO;
			break;
		case QuestionDef.QTN_TYPE_AUDIO:
			index = DT_INDEX_AUDIO;
			break;
		/*case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC:
			index = DT_INDEX_SINGLE_SELECT_DYNAMIC;
			break;*/
		case QuestionDef.QTN_TYPE_GPS:
			index = DT_INDEX_GPS;
			break;
		case QuestionDef.QTN_TYPE_BARCODE:
			index = DT_INDEX_BARCODE;
			break;
		}

		cbDataType.setSelectedIndex(index);
	}

	/**
	 * Clears values from all widgets.
	 */
	public void clearProperties(){
		cbDataType.setSelectedIndex(DT_INDEX_NONE);
		chkVisible.setValue(false);
		chkEnabled.setValue(false);
		chkLocked.setValue(false);
		chkRequired.setValue(false);
		txtDefaultValue.setText(null);
		txtHelpText.setText(null);
		txtText.setText(null);
		txtBinding.setText(null);
		txtDescTemplate.setText(null);
		txtCalculation.setText(null);
		txtFormKey.setText(null);
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormSelectionListener#onFormItemSelected(java.lang.Object)
	 */
	public void onFormItemSelected(Object formItem, TreeItem treeItem) {
		propertiesObj = formItem;
		skipRulesView.onFormItemSelected(treeItem);
		validationRulesView.onFormItemSelected(treeItem);
		dynamicListsView.onFormItemSelected(treeItem);

		clearProperties();

		//For now these may be options for boolean question types (Yes & No)
		if(formItem == null){
			enableQuestionOnlyProperties(false);
			txtText.setEnabled(false);
			//txtDescTemplate.setVisible(false);
			//btnDescTemplate.setVisible(false);
			enableDescriptionTemplate(false);

			txtBinding.setEnabled(false);

			return;
		}

		txtBinding.setEnabled(Context.allowBindEdit() && !Context.isStructureReadOnly());

		if(formItem instanceof FormDef)
			setFormProperties((FormDef)formItem);
		else if(formItem instanceof PageDef)
			setPageProperties((PageDef)formItem);
		else if(formItem instanceof QuestionDef)
			setQuestionProperties((QuestionDef)formItem);
		else if(formItem instanceof OptionDef){
			setQuestionOptionProperties((OptionDef)formItem);

			//Since option bindings are not xml node names, we may allow their
			//edits as they are not structure breaking.
			txtBinding.setEnabled(!Context.isStructureReadOnly());
		}
	}

	/**
	 * Sets focus to the first input widget.
	 */
	public void setFocus(){
		txtText.setFocus(true);
		txtText.selectAll();
	}

	/**
	 * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
	 */
	public void onWindowResized(int width, int height){
		setWidth("100%");
		setHeight("100%");
		validationRulesView.onWindowResized(width, height);
	}

	/**
	 * Retrieves changes from all widgets and updates the selected object.
	 */
	public void commitChanges(){
		skipRulesView.updateSkipRule();
		validationRulesView.updateValidationRule();
		dynamicListsView.updateDynamicLists();
	}

	/**
	 * @see org.purc.purcforms.client.controller.ItemSelectionListener#onItemSelected(Object, Object)
	 */
	public void onItemSelected(Object sender, Object item, boolean userAction) {
		if(sender == btnDescTemplate){

			item = "${" + item + "}$";

			if(propertiesObj instanceof QuestionDef){
				txtText.setText(txtText.getText() + " " + txtDescTemplate.getText() + item);
				updateText(true);
				txtText.setFocus(true);
			}
			else{
				txtDescTemplate.setText(txtDescTemplate.getText() + item);
				updateDescTemplate(true); //Added for IE which does not properly throw change events for the desc template textbox
				txtDescTemplate.setFocus(true);
			}
		}
		else if(sender == btnCalculation){
			assert(propertiesObj instanceof QuestionDef);
			txtCalculation.setText(txtCalculation.getText() + item);
			updateCalculation(true); //Added for IE which does not properly throw change events for the desc template textbox
			txtCalculation.setFocus(true);
		}
	}

	/**
	 * @see org.purc.purcforms.client.controller.ItemSelectionListener#onStartItemSelection(Object)
	 */
	public void onStartItemSelection(Object sender) {

	}

	/**
	 * Sets the listener to form action events.
	 * 
	 * @param formActionListener the listener.
	 */
	public void setFormActionListener(IFormActionListener formActionListener){
		this.formActionListener = formActionListener;
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONKEYDOWN:
			if(!isVisible())
				return;

			int keyCode = event.getKeyCode();
			if(event.getCtrlKey()){
				if(keyCode == 'N' || keyCode == 'n'){
					formActionListener.addNewItem();
					DOM.eventPreventDefault(event);
				}
				else if(keyCode == KeyCodes.KEY_RIGHT){
					formActionListener.moveToChild();
					DOM.eventPreventDefault(event);
				}
				else if(keyCode == KeyCodes.KEY_LEFT){
					formActionListener.moveToParent();
					DOM.eventPreventDefault(event);
				}
				else if(keyCode == KeyCodes.KEY_UP){
					formActionListener.moveUp();
					DOM.eventPreventDefault(event);
				}
				else if(keyCode == KeyCodes.KEY_DOWN){
					formActionListener.moveDown();
					DOM.eventPreventDefault(event);
				}
			}
		}
	}

	private void enableDescriptionTemplate(boolean enable){
		//txtDescTemplate.setVisible(enable);
		btnDescTemplate.setEnabled(enable);
		//txtDescTemplate.getParent().setVisible(enable);
		//lblDescTemplate.setVisible(enable);

		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setVisible(10, 0, enable);
		cellFormatter.setVisible(10, 1, enable);

		//form key
		cellFormatter.setVisible(11, 0, enable);
		cellFormatter.setVisible(11, 1, enable);
	}
	
	public void selectSkipRulesTab(){
		tabs.selectTab(0);
	}
	
	public void selectValidationRulesTab(){
		tabs.selectTab(1);
	}
	
	public void selectDynamicListsTab(){
		tabs.selectTab(2);
	}
}
