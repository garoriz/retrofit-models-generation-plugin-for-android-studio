package ru.kpfu.stud.rizrgaripov.netmodulegeneration.api.addparamsforhttpannotation

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class AddParamsForHttpAnnotationDialog(
    project: Project
) : DialogWrapper(project, true) {
    init {
        title = "Add Parameters For Http Annotation"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = panel {
            row {
                label("Adding parameters for HTTP annotation")
            }
            row ("Method:") {
                textField()
                    .bindText(AddParamsForHttpAnnotationService.instance.addParamsForHttpAnnotationState::method)
            }
            row {
                checkBox("Has body:")
                    .bindSelected(AddParamsForHttpAnnotationService.instance.addParamsForHttpAnnotationState::hasBody)
            }
        }
        return panel
    }
}