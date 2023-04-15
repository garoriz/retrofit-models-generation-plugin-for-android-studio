package ru.kpfu.stud.rizrgaripov.netmodulegeneration.api.addparam

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class AddParamDialog(
    project: Project
) : DialogWrapper(project, true) {
    init {
        title = "Add Parameter"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = panel {
            row {
                label("Do you need to add a parameter?")
            }
            row("Annotation  type:") {
                comboBox(AddParamService.AnnotationType.values().toList())
                    .bindItem(AddParamService.instance.addParamState::annotationType.toNullableProperty())
            }
            row("Annotation value, if needed:") {
                textField()
                    .bindText(AddParamService.instance.addParamState::annotationValue)
            }
            row("Parameter name:") {
                textField()
                    .bindText(AddParamService.instance.addParamState::paramName)
            }
            row("Return parameter data type (if custom, then full name with packages):") {
                textField()
                    .bindText(AddParamService.instance.addParamState::paramDataType)
            }
        }
        return panel
    }
}