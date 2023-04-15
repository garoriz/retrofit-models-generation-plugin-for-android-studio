package ru.kpfu.stud.rizrgaripov.netmodulegeneration.api

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import ru.kpfu.stud.rizrgaripov.netmodulegeneration.api.addparam.AddParamDialog
import ru.kpfu.stud.rizrgaripov.netmodulegeneration.api.addparam.AddParamService
import ru.kpfu.stud.rizrgaripov.netmodulegeneration.api.addparamsforhttpannotation.AddParamsForHttpAnnotationDialog
import ru.kpfu.stud.rizrgaripov.netmodulegeneration.api.addparamsforhttpannotation.AddParamsForHttpAnnotationService

class CreateApiAction : CodeInsightAction(), CodeInsightActionHandler {
    override fun getHandler(): CodeInsightActionHandler = this

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val ktClass = file.getKtClassWithEditor(editor) ?: return
        val wasDialogOK = CreateApiDialog(
            project
        ).showAndGet()
        if (wasDialogOK) {
            handleDialogOK(project, ktClass)
        }
    }

    private fun handleDialogOK(project: Project, ktClass: KtClass) {
        val state = CreateApiService.instance.createApiState
        val addParamsForHttpAnnotationState = AddParamsForHttpAnnotationService.instance.addParamsForHttpAnnotationState
        val addParamState = AddParamService.instance.addParamState
        val psiFactory = KtPsiFactory(project)
        project.executeWriteCommand("GenerateApi") {
            var annotationHttpMethodString = "@retrofit2.http.${state.httpMethod}"
            if (state.httpMethod == CreateApiService.HttpMethod.HTTP) {
                annotationHttpMethodString += getAnnotationHttpMethodValues(
                    state,
                    addParamsForHttpAnnotationState,
                    project
                )
            } else if (state.url != "")
                annotationHttpMethodString += getUrl(state)

            var funcString = ""
            if (state.asyncSupport == CreateApiService.AsyncSupport.SUSPEND)
                funcString += getSuspendString(state)
            funcString += "fun ${state.funcName}("
            val wasDialogOK = AddParamDialog(
                project
            ).showAndGet()
            if (wasDialogOK)
                funcString += getParamsString(addParamState, project)
            funcString += ")"
            funcString += when (state.asyncSupport) {
                CreateApiService.AsyncSupport.SINGLE -> ": ${state.asyncSupport.string}<${state.returnValDataType}>"
                CreateApiService.AsyncSupport.COMPLETABLE -> ": ${state.asyncSupport.string}"
                else -> {
                    ": ${state.returnValDataType}"
                }
            }

            val func = psiFactory.createFunction(funcString)

            val annotationHttpMethod = psiFactory.createAnnotationEntry(annotationHttpMethodString)
            func.addAnnotationEntry(annotationHttpMethod)
            if (state.isFormEncoded)
                addAnnotationFormUrlEncoded(psiFactory, func)
            if (state.isMultipart)
                addAnnotationMultipart(psiFactory, func)
            if (state.isStreaming)
                addAnnotationStreaming(psiFactory, func)
            ktClass.addDeclaration(func)

            ShortenReferences.DEFAULT.process(ktClass.containingKtFile)
            CodeStyleManager.getInstance(project).reformat(ktClass.containingKtFile)
        }
    }

    override fun startInWriteAction(): Boolean = false

    private fun PsiFile.getKtClassWithEditor(editor: Editor): KtClass? {
        val offset = editor.caretModel.offset
        val psiElement = findElementAt(offset)
        return PsiTreeUtil.getParentOfType(psiElement, KtClass::class.java)
    }

    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    private fun getAnnotationHttpMethodValues(
        state: CreateApiService.State,
        addParamsForHttpAnnotationState: AddParamsForHttpAnnotationService.State,
        project: Project
    ): String {
        var values = "("
        if (state.url != "")
            values += getPath(state)
        values += "method = \""
        val wasDialogOK = AddParamsForHttpAnnotationDialog(
            project
        ).showAndGet()
        if (wasDialogOK)
            values += getMethod(addParamsForHttpAnnotationState)
        values += "\", hasBody = ${addParamsForHttpAnnotationState.hasBody})"
        return values
    }

    private fun getPath(state: CreateApiService.State): String {
        return "path = \"${state.url}\", "
    }

    private fun getMethod(addParamsForHttpAnnotationState: AddParamsForHttpAnnotationService.State): String {
        return addParamsForHttpAnnotationState.method
    }

    private fun getUrl(state: CreateApiService.State): String {
        return "(\"${state.url}\")"
    }

    private fun getSuspendString(state: CreateApiService.State): String {
        return "${state.asyncSupport.string} "
    }

    private fun getParamsString(addParamState: AddParamService.State, project: Project): String {
        var paramsString: String = if ((addParamState.annotationType == AddParamService.AnnotationType.BODY)
                .or(addParamState.annotationType == AddParamService.AnnotationType.FIELD_MAP)
                .or(addParamState.annotationType == AddParamService.AnnotationType.PART_MAP)
                .or(addParamState.annotationType == AddParamService.AnnotationType.TAG)
                .or(addParamState.annotationType == AddParamService.AnnotationType.URL)
                .or(addParamState.annotationType == AddParamService.AnnotationType.QUERY_MAP)
                .or(addParamState.annotationType == AddParamService.AnnotationType.QUERY_NAME)
        )
            "@${addParamState.annotationType.string} ${addParamState.paramName}: " +
                    addParamState.paramDataType
        else "@${addParamState.annotationType.string}(\"${addParamState.annotationValue}\") ${addParamState.paramName}: " +
                addParamState.paramDataType
        addParamState.paramName = ""
        addParamState.paramDataType = ""
        addParamState.annotationValue = ""
        var wasDialogOK = AddParamDialog(
            project
        ).showAndGet()
        while (wasDialogOK) {
            paramsString += addParamsString(addParamState)
            wasDialogOK = AddParamDialog(
                project
            ).showAndGet()
        }
        return paramsString
    }

    private fun addParamsString(addParamState: AddParamService.State): String {
        val paramsString: String = if ((addParamState.annotationType == AddParamService.AnnotationType.BODY)
                .or(addParamState.annotationType == AddParamService.AnnotationType.FIELD_MAP)
                .or(addParamState.annotationType == AddParamService.AnnotationType.PART_MAP)
                .or(addParamState.annotationType == AddParamService.AnnotationType.TAG)
                .or(addParamState.annotationType == AddParamService.AnnotationType.URL)
                .or(addParamState.annotationType == AddParamService.AnnotationType.QUERY_MAP)
                .or(addParamState.annotationType == AddParamService.AnnotationType.QUERY_NAME)
        )
            ", @${addParamState.annotationType.string} ${addParamState.paramName}: ${addParamState.paramDataType}"
        else ", @${addParamState.annotationType.string}(\"${addParamState.annotationValue}\") " +
                "${addParamState.paramName}: ${addParamState.paramDataType}"
        addParamState.paramName = ""
        addParamState.paramDataType = ""
        addParamState.annotationValue = ""
        return paramsString
    }

    private fun addAnnotationFormUrlEncoded(psiFactory: KtPsiFactory, func: KtNamedFunction) {
        val annotationFormEncoded = psiFactory.createAnnotationEntry("@retrofit2.http.FormUrlEncoded")
        func.addAnnotationEntry(annotationFormEncoded)
    }

    private fun addAnnotationMultipart(psiFactory: KtPsiFactory, func: KtNamedFunction) {
        val annotationMultipart = psiFactory.createAnnotationEntry("@retrofit2.http.Multipart")
        func.addAnnotationEntry(annotationMultipart)
    }

    private fun addAnnotationStreaming(psiFactory: KtPsiFactory, func: KtNamedFunction) {
        val annotationMultipart = psiFactory.createAnnotationEntry("@retrofit2.http.Streaming")
        func.addAnnotationEntry(annotationMultipart)
    }
}