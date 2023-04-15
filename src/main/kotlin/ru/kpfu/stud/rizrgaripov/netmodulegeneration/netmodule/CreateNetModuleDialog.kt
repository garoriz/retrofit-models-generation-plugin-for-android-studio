package ru.kpfu.stud.rizrgaripov.netmodulegeneration.netmodule

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class CreateNetModuleDialog(
    project: Project
) : DialogWrapper(project, true) {
    init {
        title = "Create Net Module"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel: DialogPanel = panel {
            row("Base URL:") {
                textField()
                    .bindText(CreateNetModuleService.instance.createNetModuleState::baseUrl)
            }
            row {
                checkBox("RxJava support::")
                    .bindSelected(CreateNetModuleService.instance.createNetModuleState::isSupportRxJava)
            }
            row("Full API class name with packages:") {
                textField()
                    .bindText(CreateNetModuleService.instance.createNetModuleState::apiClassName)
            }
        }
        return panel
    }

}