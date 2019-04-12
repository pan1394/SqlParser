package com.linkstec.bee.UI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.linkstec.bee.UI.look.icon.BeeIcon;

public class BeeConstants {
	public static final int DEVIDER_SIZE = BeeUIUtils.getDefaultFontSize() / 3;
	public static final JFrame FONT_METRIC_FRAME = new JFrame();
	public static final Font DEFUALT_FONT = new Font(BeeConstants.LABEL_DEFAULT_FONT, BeeConstants.LABEL_DEFAULT_STYLE,
			BeeConstants.LABEL_DEFAULT_SIZE);
	public static final double SEGMENT_MAX_WIDTH = 800;
	public static final double DEFAULT_END_Y = 800;
	public static final int LINE_HEIGHT = 20;
	public static final int FOLDED_WIDTH = 200;
	public static final int ELEMENT_TITLE_WIDTH = 120;
	public static final int ELEMENT_VALUE_WIDTH = 120;
	public static final int METHOD_TITLE_WIDTH = 200;
	public static final int ELEMENT_VALUED_WIDTH = 50;
	public static final int NODE_SPACING = 20;
	public static final int VALUE_NODE_SPACING = 4;
	public static final int VALUE_INNER_NODE_SPACING = 8;
	public static final int CONNECTOR_SIZE = 6;
	public static final int NODE_TITEL_WIDTH = 400;
	public static final int SEGMENT_EDITOR_DEFAULT_HEIGHT = 80;
	public static final int SEGMENT_EDITOR_DEFAULT_WIDTH = 800;
	public static final int INNER_EDITOR_DEFAULT_WIDTH = 400;
	public static final int ACTOR_WIDTH = 40;
	public static final int STARTER_SIZE = 50;
	public static final int PAGE_SPACING_LEFT = 50;
	public static final int PAGE_SPACING_RIGHT = 20;
	public static final int PAGE_SPACING_TOP = 20;
	public static final int PAGE_MAX_HEIGHT = 4000;
	public static final int PAGE_SPACING_BOTTOM = 10;
	public static final int PAGE_SEGMENT_GAP = 15;
	public static final String VALUE_WAIT_COLOR = "F3F5FC";

	public static final String VALUe_ADDED_COLOR = "F0F8FF";
	public static final String TYPE_NODE_COLOR = "0099FF";

	public static final String ELEGANT_BLUE_COLOR = "3399FF";
	public static final String ELEGANT_YELLOW_COLOR = "FF9933";
	public static final String ELEGANT_GREEN_COLOR = "66CC33";

	public static final String ELEGANT_BRIGHTER_BLUE_COLOR = "8CC6FF";
	public static final String ELEGANT_BRIGHTER_YELLOW_COLOR = "FFCB97";
	public static final String ELEGANT_BRIGHTER_GREEN_COLOR = "9CDE7C";

	public static final String BLOCK_TITLE_COLOR = "F4F7FC";// "D0DFEF";
	public static final String BLOCK_TITLE_GREDIENT_COLOR = "E4E9F7";// "E8F1FB";
	public static final Color EXPLORE_VALUE_COLOR = Color.decode("#957D47");
	public static final Color BORDER_BACKCOLOR = Color.decode("#FCFCFC");
	public static final Color BORDER_ERROR_BACKCOLOR = Color.decode("#F9BADA");
	public static final Color BORDER_ERROR_BORDERCOLOR = Color.decode("#FD3198");

	public static final Color BORDER_ALERT_BACKCOLOR = Color.decode("#FBF5E9");
	public static final Color BORDER_ALERT_BORDERCOLOR = Color.decode("#F3DFB9");

	public static final Color MOUSEOVER_BACKGROUND_COLOR = Color.decode("#E5F3FF");
	public static final Color SELECTED_BACKGROUND_COLOR = Color.decode("#99CCFF");
	public static final Color BACKGROUND_COLOR = Color.decode("#E4E9F7");
	public static final Color TOOLBAR_GREDIENT_UP = Color.decode("#F4F7FC");
	public static final Color TOOLBAR_GREDIENT_DOWN = Color.decode("#E4E9F7");
	public static final Color TABBEDPANE_FOCUSED_COLOR = Color.decode("#D0DFEE");
	public static final Color TABBEDPANE_UNFOCUSED_COLOR = Color.decode("#F4F6FC");
	// Color.decode("#F4F6FC")
	public static final String LABEL_DEFAULT_FONT = "Meiryo UI";
	public static final int LABEL_DEFAULT_STYLE = Font.PLAIN;
	public static final int LABEL_DEFAULT_SIZE = 10;
	public static final int HEIGHT_BETWEEN_BLOCK = 3;

	public static final ImageIcon PROJECT_IMPORT = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/project_import.gif"));

	public static final ImageIcon REFERENCE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/reference.gif"));
	public static final ImageIcon GROUP_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/group.gif"));
	public static final ImageIcon NEXT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/next.gif"));
	public static final ImageIcon MAP_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/map.gif"));
	public static final ImageIcon LIST_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/list.gif"));

	public static final ImageIcon NAMING_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/naming.gif"));
	public static final ImageIcon CONSTANT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/constant.gif"));
	public static final ImageIcon PACKAGE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/package.gif"));
	public static final ImageIcon CODE_CONFIG_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/code_config.gif"));
	public static final ImageIcon IO_CONFIG_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/inout.gif"));
	public static final ImageIcon RIGHT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/rightarrow.gif"));
	public static final ImageIcon NODE_DB_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/node_db.gif"));
	public static final ImageIcon NODE_FILE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/node_file.gif"));
	public static final ImageIcon NODE_SESSION_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/node_session.gif"));
	public static final ImageIcon NODE_PAGE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/node_text.gif"));
	public static final ImageIcon NODE_APPLICATION_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/node_application.png"));
	public static final ImageIcon NODE_MESSAGE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/node_message.gif"));

	public static final ImageIcon GREEN_STAR_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/green_start.gif"));

	public static final ImageIcon FLOW_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/flow.gif"));
	public static final ImageIcon FLOW_MODEL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/flow_model.gif"));
	public static final ImageIcon IO_MODEL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/io_model.gif"));

	public static final ImageIcon TABLES_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/tables.gif"));
	public static final ImageIcon RESOURCE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/resource.gif"));
	public static final ImageIcon BASIC_DESIGN_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/basic_design.gif"));

	public static final ImageIcon ZOOM_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/zoom.png"));
	public static final ImageIcon NEW_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/new.gif"));
	public static final ImageIcon SAVE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/save.gif"));
	public static final ImageIcon SAVEAS_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/saveas.gif"));
	public static final ImageIcon EXIT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/exit.gif"));
	public static final ImageIcon PAGESETUP_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/pagesetup.gif"));
	public static final ImageIcon PRINT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/print.gif"));
	public static final ImageIcon UNDO_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/undo.gif"));
	public static final ImageIcon REDO_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/redo.gif"));
	public static final ImageIcon CUT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/cut.gif"));
	public static final ImageIcon COPY_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/copy.gif"));
	public static final ImageIcon MENU_PASTE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/paste.gif"));
	public static final ImageIcon DELETE_WHITE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/delete_white.gif"));
	public static final ImageIcon DELETE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/delete.gif"));
	public static final ImageIcon CELL_SELECT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/cellselect.gif"));
	public static final ImageIcon SELECTALL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/selectall.gif"));
	public static final ImageIcon SELECTNONE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/selectnone.gif"));
	public static final ImageIcon HELP_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/help.gif"));

	public static final ImageIcon SYNCHRONIZE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/synchronize.gif"));
	public static final ImageIcon STATIC_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/static.gif"));

	public static final ImageIcon LABEL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/label.gif"));
	public static final ImageIcon STRING_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/string.gif"));

	public static final ImageIcon INTERFACE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/interface.gif"));
	public static final ImageIcon ARRAY_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/array.gif"));
	public static final ImageIcon CLASS_PROTECTED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/class_protected.gif"));
	public static final ImageIcon CLASS_ABSTRACT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/class_abstract.gif"));
	public static final ImageIcon CLASS_PRIVATE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/class_private.gif"));
	public static final ImageIcon CLASS_ATTRIBUTE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/att_class.gif"));
	public static final ImageIcon ANNOTATION_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/annotation.gif"));
	public static final ImageIcon ANNOTATION_PARAMETER_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/annotation_para.gif"));

	public static final ImageIcon USER_ACTION_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/user_action.gif"));
	public static final ImageIcon ACTION_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/action.gif"));
	public static final ImageIcon CLEANUP_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/cleanup.gif"));
	public static final ImageIcon START_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/start.gif"));
	public static final ImageIcon STOP_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/stop.gif"));
	public static final ImageIcon SUSPEND_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/suspend.gif"));
	public static final ImageIcon EXPORT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/export.gif"));
	public static final ImageIcon CHECKBOX_SELECTED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/checkbox_selected.gif"));
	public static final ImageIcon CHECKBOX_UNSELECTED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/checkbox_unselected.gif"));
	public static final ImageIcon CHECKBOX_DISABLED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/checkbox_disabled.gif"));
	public static final ImageIcon COMPILE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/task_in_progress.gif"));
	public static final ImageIcon REFRESH_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/refresh.gif"));
	public static final ImageIcon WARNING_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/warning.gif"));
	public static final ImageIcon NEW_PROJECT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/create_new_project.gif"));
	public static final ImageIcon SOURCE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/source.gif"));
	public static final ImageIcon PROPERTY_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/property.gif"));
	public static final ImageIcon CONSOLE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/console.gif"));
	public static final ImageIcon PROBLEMS_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/problems.gif"));
	public static final ImageIcon EXCEL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/excel.gif"));
	public static final ImageIcon SEARCH_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/search.gif"));
	public static final ImageIcon JAVA_SERACH_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/java_search.gif"));
	public static final ImageIcon GRAPH_SEARCH_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/graph_search.gif"));
	public static final ImageIcon TREE_FOLDER_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/package_obj.gif"));
	public static final ImageIcon LOGIKER_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/logiker.gif"));
	public static final ImageIcon BASIC_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/basic.gif"));
	public static final ImageIcon PARAMETERS_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/parameters.gif"));
	public static final ImageIcon COMMON_METHOD_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/method_obj.gif"));
	public static final ImageIcon TREE_NODE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/node.png"));
	public static final ImageIcon EXPLORE_FILE_TILE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/package.gif"));
	public static final ImageIcon EXPLORE_OUTLINE_TILE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/outline_co.gif"));
	public static final ImageIcon PLATTE_EDIT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/edit_node.png"));
	public static final ImageIcon GREEN_PLUS_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/plus.png"));
	public static final ImageIcon MINUS_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/minus.png"));
	public static final ImageIcon ERROR_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/error.gif"));
	public static final ImageIcon SPACE_TAB_CLOSE_BTN_OFF = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/notification-close.gif"));
	public static final ImageIcon SPACE_TAB_CLOSE_BTN_ON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/notification-close-active.gif"));
	public static final ImageIcon SHEET_DATA_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/sheet_data_co.gif"));
	public static final ImageIcon SHEET_LOGIC_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/sheet_logic_co.gif"));
	public static final ImageIcon APP_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icon.png"), 30, 30);
	public static final ImageIcon VAR_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/var.gif"));
	public static final ImageIcon VAR_COLUMN_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/column_var.gif"));
	public static final ImageIcon VAR_COLUMN_CELL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/column_cell_var.gif"));
	public static final ImageIcon METHOD_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/methpub_obj.gif"));
	public static final ImageIcon METHOD_PROTECED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/method_protected.gif"));
	public static final ImageIcon METHOD_PRIVATE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/method_private.gif"));
	public static final ImageIcon METHOD_STATIC_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/static_method.gif"));

	public static final ImageIcon VAR_PRIVATE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/field_private_obj.gif"));
	public static final ImageIcon VAR_PUBLIC_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/field_public_obj.gif"));
	public static final ImageIcon VAR_PROTECED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/field_protected_obj.gif"));
	public static final ImageIcon VAR_STATIC_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/static_property.gif"));
	public static final ImageIcon VAR_PARAMETER_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/parameter.gif"));
	public static final ImageIcon VAR_LOCAL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/localvariable.gif"));

	public static final ImageIcon STATER_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/start_task.gif"));
	public static final ImageIcon END_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/end_obj.gif"));
	public static final ImageIcon DATA_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/datasheet.gif"));
	public static final ImageIcon ADD_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/add_exc.gif"));
	public static final ImageIcon ADD_ON_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/add_obj.gif"));
	public static final ImageIcon EMPTY_16X16_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/empty.gif"), 16, 16);
	public static final ImageIcon ALERT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/alert_obj.gif"));
	public static final ImageIcon TREE_EXPANDED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/tree-expanded.png"));
	public static final ImageIcon TREE_COLLAPSED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/tree-collapsed.png"));
	public static final ImageIcon WIN_MAX_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/maximize_winmng.gif"));
	public static final ImageIcon WIN_MIN_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/min.gif"));
	public static final ImageIcon WIN_MENU_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/view_menu.gif"));
	public static final ImageIcon PERSPECTIVE_DESIGN_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/graph.gif"));
	public static final ImageIcon PERSPECTIVE_JAVA_SOURCE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/java_app.gif"));
	public static final ImageIcon JAVA_SOURCE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/java_file.gif"));
	public static final ImageIcon GENERATE_CODE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/generate_code.gif"));
	public static final ImageIcon GENERATE_GRAPH_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/generate_graph.gif"));
	public static final ImageIcon CONFIG_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/modules_view.gif"));
	public static final ImageIcon JAR_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/jar.gif"));
	public static final ImageIcon LIB_FOLDER_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/library_folder.gif"));
	public static final ImageIcon FILES_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/file.gif"));
	public static final ImageIcon TYPE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/type.gif"));
	public static final ImageIcon FUNCTION_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/function.gif"));
	public static final ImageIcon CLASSES_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/classes_obj.gif"));
	public static final ImageIcon COMMON_METHOD_CLASS_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/openmethod.gif"));
	public static final ImageIcon TYPE_DEF_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/typedef_obj.gif"));
	public static final ImageIcon YES_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/output_yes.gif"));
	public static final ImageIcon NO_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/output_no.gif"));
	public static final ImageIcon LIST_DELETE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/list-delete.gif"));
	public static final ImageIcon LIST_ADD_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/list-add.gif"));
	public static final ImageIcon PASTE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/paste.gif"));
	public static final ImageIcon DATABASE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/database.gif"));
	public static final ImageIcon CLEAR_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/clear_co.gif"));
	public static final ImageIcon PROGRESS_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/progress.gif"));
	public static final ImageIcon SELECT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/select.gif"));
	public static final ImageIcon BOOK_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/book.gif"));
	public static final ImageIcon FOLDER_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/folder.gif"));
	public static final ImageIcon EMPTY_DIR_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/dir_empty.gif"));
	public static final ImageIcon PROJECT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/project.gif"));
	public static final ImageIcon PROJECT_CLOSE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/project_close.gif"));
	public static final ImageIcon INITIALIZE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/initialize.gif"));
	public static final ImageIcon FINAL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/final.gif"));
	public static final ImageIcon CATCH_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/catch.gif"));
	public static final ImageIcon THROW_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/throw.gif"));
	public static final ImageIcon CLOSE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/close.gif"));
	public static final ImageIcon CLOSEALL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/close_all.gif"));

	public static final ImageIcon P_ASSIGN_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/assign.gif"));
	public static final ImageIcon P_BREAK_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/break.gif"));
	public static final ImageIcon P_CONTINUE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/continue.gif"));
	public static final ImageIcon P_CATCH_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/catch.gif"));
	public static final ImageIcon P_CHOICE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/choice.gif"));
	public static final ImageIcon P_FORMULA_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/formula.gif"));
	public static final ImageIcon P_LINE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/line.gif"));
	public static final ImageIcon P_METHOD_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/method.gif"));
	public static final ImageIcon P_RETURN_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/return.gif"));
	public static final ImageIcon P_LOOP_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/loop.gif"));
	public static final ImageIcon P_NOTE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/note.gif"));
	public static final ImageIcon P_EXPRESSION_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/expression.gif"));

	public static final ImageIcon P_INCREASE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/increase.gif"));
	public static final ImageIcon P_DECREASE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/decrease.gif"));
	public static final ImageIcon ASSIGN_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/assign.gif"));
	public static final ImageIcon P_TITLE_VAR_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/variables.gif"));
	public static final ImageIcon P_TITLE_INVOKE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/invoke.gif"));
	public static final ImageIcon P_TITLE_EIDTOR_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/editor.gif"));
	public static final ImageIcon P_TITLE_COMMENT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/comment.gif"));
	public static final ImageIcon P_TRUEFALSE_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/truefalse.gif"));
	public static final ImageIcon P_FRONT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/front.gif"));

	public static final ImageIcon TYPE_STRING_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/type_string.gif"));
	public static final ImageIcon TYPE_COMPLEX_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/type_complex.gif"));
	public static final ImageIcon TYPE_DEFINED_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/type_defined.gif"));
	public static final ImageIcon TYPE_DIGITAL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/type_digital.gif"));
	public static final ImageIcon TYPE_OTHERS_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/type_others.gif"));
	public static final ImageIcon TYPE_CONTAINER_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/type_container.gif"));
	public static final ImageIcon TYPE_LOGICAL_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/type_logical.gif"));
	public static final ImageIcon TYPE_OBJECT_ICON = new BeeIcon(
			BeeConstants.class.getResource("/com/linkstec/bee/UI/images/palette/type_object.gif"));

}
