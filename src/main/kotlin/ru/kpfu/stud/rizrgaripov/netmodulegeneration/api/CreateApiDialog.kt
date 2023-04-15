package ru.kpfu.stud.rizrgaripov.netmodulegeneration.api

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class CreateApiDialog(
    project: Project
) : DialogWrapper(project, true) {
    init {
        title = "Create Api"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row("HTTP method:") {
                comboBox(CreateApiService.HttpMethod.values().toList())
                    .bindItem(CreateApiService.instance.createApiState::httpMethod.toNullableProperty())
            }
            row("A relative or absolute path, or full URL of the endpoint:") {
                textField()
                    .bindText(CreateApiService.instance.createApiState::url)
            }
            row {
                checkBox("Form encoded:")
                    .bindSelected(CreateApiService.instance.createApiState::isFormEncoded)
            }
            row {
                checkBox("Multipart:")
                    .bindSelected(CreateApiService.instance.createApiState::isMultipart)
            }
            row {
                checkBox("Streaming:")
                    .bindSelected(CreateApiService.instance.createApiState::isStreaming)
            }
            row("Async support:") {
                comboBox(CreateApiService.AsyncSupport.values().toList())
                    .bindItem(CreateApiService.instance.createApiState::asyncSupport.toNullableProperty())
            }
            row("Function name:") {
                textField()
                    .bindText(CreateApiService.instance.createApiState::funcName)
            }
            row("Return value data type (if custom, then full name with packages):") {
                textField()
                    .bindText(CreateApiService.instance.createApiState::returnValDataType)
            }
        }
    }
}