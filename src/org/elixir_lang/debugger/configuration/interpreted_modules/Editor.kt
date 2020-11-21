package org.elixir_lang.debugger.configuration.interpreted_modules

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.TableUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import org.elixir_lang.debugger.Settings
import org.elixir_lang.debugger.configuration.Debuggable
import org.elixir_lang.debugger.configuration.InheritedModuleFilter
import org.elixir_lang.debugger.configuration.inherited_module_filter.table.Model
import org.elixir_lang.debugger.configuration.inherited_module_filter.table.cell_renderer.Pattern
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ItemEvent
import javax.swing.*

class Editor<T : Debuggable<*>> : SettingsEditor<T>() {
    private lateinit var inheritApplicationModuleFiltersCheckBox: JCheckBox
    private lateinit var inheritedModuleFilterTableModel: Model
    private lateinit var inheritedModuleFilterTable: JBTable

    override fun resetEditorFrom(configuration: T) {
        val inheritApplicationModuleFilters = configuration.inheritApplicationModuleFilters

        inheritApplicationModuleFiltersCheckBox.isSelected = inheritApplicationModuleFilters

        val inheritedPatternToModuleFilter = if (inheritApplicationModuleFilters) {
            Settings.getInstance().moduleFilters.associateBy { it.pattern }
        } else {
            emptyMap()
        }

        val configuredPatternToModuleFilter = configuration.moduleFilterList.associateBy { it.pattern }

        val inheritedPatternSet = inheritedPatternToModuleFilter.keys
        val configuredPatternSet = configuredPatternToModuleFilter.keys
        val patternList = inheritedPatternSet.union(configuredPatternSet).sorted()

        inheritedModuleFilterTableModel.inheritedModuleFilterList = patternList.map { pattern ->
            val inheritedModuleFilter = inheritedPatternToModuleFilter[pattern]
            val configuredModuleFilter = configuredPatternToModuleFilter[pattern]

            val inherited = inheritedModuleFilter != null
            val enabled = configuredModuleFilter?.enabled ?: inheritedModuleFilter!!.enabled

            InheritedModuleFilter(inherited, enabled, pattern)
        }.toMutableList()
    }

    override fun createEditor(): JComponent =
            JPanel(BorderLayout()).apply {
                add(createInheritApplicationModuleFiltersCheckBox(), BorderLayout.NORTH)
                add(createInheritedModuleFilterPanel(), BorderLayout.CENTER)
            }

    override fun applyEditorTo(configuration: T) {
        val inheritApplicationModuleFilters = inheritApplicationModuleFiltersCheckBox.isSelected

        configuration.inheritApplicationModuleFilters = inheritApplicationModuleFilters

        val inheritedPatternToModuleFilter = if (inheritApplicationModuleFilters) {
            Settings.getInstance().moduleFilters.associateBy { it.pattern }
        } else {
            emptyMap()
        }

        configuration.moduleFilterList =
                inheritedModuleFilterTableModel
                        .inheritedModuleFilterList
                        .mapNotNull { editorInheritedModuleFilter ->
                            val pattern = editorInheritedModuleFilter.pattern
                            val inheritedModuleFilter = inheritedPatternToModuleFilter[pattern]
                            val editorEnabled = editorInheritedModuleFilter.enabled

                            val inheritedEnabled =
                            // record differing enabled for inherited patterns
                                    inheritedModuleFilter?.enabled
                                    // only record enabled for non-inherited patterns
                                            ?: false

                            if (editorEnabled != inheritedEnabled) {
                                ModuleFilter(editorEnabled, pattern)
                            } else {
                                null
                            }
                        }
                        .toMutableList()
    }

    private fun createInheritApplicationModuleFiltersCheckBox(): JComponent {
        inheritApplicationModuleFiltersCheckBox = JCheckBox("Inherit Application Module Filters")
        inheritApplicationModuleFiltersCheckBox.toolTipText = "Inherit Application Module Filters from Preferences > Build, Execution, Deployment > Debugger > Stepping > Elixir"
        inheritApplicationModuleFiltersCheckBox.addItemListener { itemEvent ->
            resetInheritedModuleFilterList(itemEvent.stateChange == ItemEvent.SELECTED)
        }

        return inheritApplicationModuleFiltersCheckBox
    }

    private fun createInheritedModuleFilterPanel(): JComponent =
            JPanel(BorderLayout()).apply {
                add(JLabel("Do not interpret modules matching patterns:"), BorderLayout.NORTH)
                add(createInheritedModuleFilterDecorator(), BorderLayout.CENTER)
            }

    private fun createInheritedModuleFilterDecorator(): JComponent =
            createInheritedModuleFilterTable().let { table ->
                ToolbarDecorator
                        .createDecorator(table)
                        .setAddAction { addPatternFilter() }
                        .setRemoveAction {
                            TableUtil.removeSelectedItems(table) { model, row ->
                                model.isCellEditable(row, Model.PATTERN_COLUMN_INDEX)
                            }
                        }
                        .setButtonComparator("Add", "Remove")
                        .createPanel()
            }

    private fun createInheritedModuleFilterTable(): JTable {
        inheritedModuleFilterTableModel = Model()

        inheritedModuleFilterTable = JBTable(inheritedModuleFilterTableModel).apply {
            setShowGrid(false)
            autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
            columnSelectionAllowed = false
            preferredScrollableViewportSize = Dimension(200, rowHeight * JBTable.PREFERRED_SCROLLABLE_VIEWPORT_HEIGHT_IN_ROWS)

            tableHeader = null
            columnModel.getColumn(Model.ENABLED_COLUMN_INDEX).let { column ->
                TableUtil.setupCheckboxColumn(column, columnModel.columnMargin)
                column.cellEditor = BooleanTableCellEditor()
                column.cellRenderer = BooleanTableCellRenderer()
            }
            columnModel.getColumn(Model.PATTERN_COLUMN_INDEX).cellRenderer = Pattern()
        }

        return inheritedModuleFilterTable
    }

    private fun resetInheritedModuleFilterList(inheritApplicationModuleFilters: Boolean) {
        val inheritedPatternToModuleFilter = if (inheritApplicationModuleFilters) {
            Settings.getInstance().moduleFilters.associateBy { it.pattern }
        } else {
            emptyMap()
        }

        val tablePatternToInheritedModuleFilter =
                inheritedModuleFilterTableModel
                        .inheritedModuleFilterList
                        .associateBy(InheritedModuleFilter::pattern)

        val inheritedPatternSet = inheritedPatternToModuleFilter.keys
        val tablePatternSet = tablePatternToInheritedModuleFilter.keys
        val patternList = inheritedPatternSet.union(tablePatternSet).sorted()

        inheritedModuleFilterTableModel.inheritedModuleFilterList = patternList.mapNotNull { pattern ->
            val tableInheritedModuleFilter = tablePatternToInheritedModuleFilter[pattern]
            val inheritedModuleFilter = inheritedPatternToModuleFilter[pattern]

            if (tableInheritedModuleFilter != null) {
                if (inheritedModuleFilter != null) {
                    if (tableInheritedModuleFilter.inherited) {
                        tableInheritedModuleFilter
                    } else {
                        InheritedModuleFilter(inherited = true, enabled = tableInheritedModuleFilter.enabled, pattern = pattern)
                    }
                } else {
                    if (tableInheritedModuleFilter.inherited) {
                        null
                    } else {
                        tableInheritedModuleFilter
                    }
                }
            } else {
                inheritedModuleFilter!!

                InheritedModuleFilter(inherited = true, enabled = inheritedModuleFilter.enabled, pattern = inheritedModuleFilter.pattern)
            }
        }.toMutableList()
    }

    private fun addPatternFilter() {
        val inheritedModuleFilter = InheritedModuleFilter(inherited = false, pattern = "*")

        inheritedModuleFilterTableModel.addRow(inheritedModuleFilter).let { row ->
            selectRow(row)
            scrollToRow(row)
        }

        focus()
    }

    private fun selectRow(row: Int) {
        inheritedModuleFilterTable.selectionModel.setSelectionInterval(row, row)
    }

    private fun scrollToRow(row: Int) {
        inheritedModuleFilterTable.scrollRectToVisible(inheritedModuleFilterTable.getCellRect(row, 0, true))
    }

    private fun focus() {
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
            IdeFocusManager.getGlobalInstance().requestFocus(inheritedModuleFilterTable, true)
        }
    }
}
