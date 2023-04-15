package ru.kpfu.stud.rizrgaripov.netmodulegeneration.netmodule.addingheader

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class AddHeaderDialog(
    project: Project
) : DialogWrapper(project, true) {
    init {
        title = "Add Header"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = panel {
            row {
                label("Do you need to add a header?")
            }
            row ("Name:") {
                textField()
                    .bindText(AddHeaderService.instance.addHeaderState::headerName)
            }
            row ("Value:") {
                textField()
                    .bindText(AddHeaderService.instance.addHeaderState::headerValue)
            }
        }
        return panel
    }
}