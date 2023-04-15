package ru.kpfu.stud.rizrgaripov.netmodulegeneration.netmodule

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
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtPsiFactory
import ru.kpfu.stud.rizrgaripov.netmodulegeneration.netmodule.addingheader.AddHeaderDialog
import ru.kpfu.stud.rizrgaripov.netmodulegeneration.netmodule.addingheader.AddHeaderService

class CreateNetModuleAction : CodeInsightAction(), CodeInsightActionHandler {
    override fun getHandler(): CodeInsightActionHandler = this


    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val ktClass = file.getKtClassWithEditor(editor) ?: return
        val wasDialogOK = CreateNetModuleDialog(
            project
        ).showAndGet()
        if (wasDialogOK) {
            handleDialogOK(project, ktClass)
        }
    }

    private fun handleDialogOK(project: Project, ktClass: KtClass) {
        val state = CreateNetModuleService.instance.createNetModuleState
        val addHeaderState = AddHeaderService.instance.addHeaderState
        val psiFactory = KtPsiFactory(project)
        project.executeWriteCommand("GenerateNetModule") {
            val getOkHttpFuncString = getOkHttpFuncString(project, addHeaderState)

            val annotationModule = psiFactory.createAnnotationEntry("@dagger.Module")
            ktClass.addAnnotationEntry(annotationModule)

            val valBaseUrl = psiFactory.createDeclaration<KtDeclaration>("private val baseUrl = " +
                    "\"${state.baseUrl}\"")
            ktClass.addDeclaration(valBaseUrl)

            val getOkHttpFunc = psiFactory.createFunction(getOkHttpFuncString)
            val annotationProvides = psiFactory.createAnnotationEntry("@dagger.Provides")
            getOkHttpFunc.addAnnotationEntry(annotationProvides)
            ktClass.addDeclaration(getOkHttpFunc)

            val provideGsonConverterFunc = psiFactory.createFunction(
                "fun provideGsonConverter(): " +
                        "retrofit2.converter.gson.GsonConverterFactory = GsonConverterFactory.create()"
            )
            provideGsonConverterFunc.addAnnotationEntry(annotationProvides)
            ktClass.addDeclaration(provideGsonConverterFunc)

            var provideApiFuncString = "fun provideApi(\n" +
                    "        provideOkHttpClient: OkHttpClient,\n" +
                    "        provideGsonConverterFactory: GsonConverterFactory,\n" +
                    "    ): ${state.apiClassName} = retrofit2.Retrofit.Builder()\n" +
                    "        .baseUrl(baseUrl)\n" +
                    "        .client(provideOkHttpClient)\n" +
                    "        .addConverterFactory(provideGsonConverterFactory)\n"
            if (state.isSupportRxJava)
                provideApiFuncString += getCallAdapterFactoryString()
            provideApiFuncString += ".build()\n" +
                    "        .create(${state.apiClassName}::class.java)"
            val provideApiFunc = psiFactory.createFunction(provideApiFuncString)
            provideApiFunc.addAnnotationEntry(annotationProvides)
            ktClass.addDeclaration(provideApiFunc)

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

    private fun getOkHttpFuncString(project: Project, addHeaderState: AddHeaderService.State): String {
        var getOkHttpFuncString = "fun getOkHttp(): okhttp3.OkHttpClient =\n" +
                "OkHttpClient.Builder()\n"
        val wasDialogOK = AddHeaderDialog(
            project
        ).showAndGet()
        if (wasDialogOK) {
            getOkHttpFuncString += createInterceptorAndAddHeader(addHeaderState, project)
        }
        getOkHttpFuncString += ".build()"
        return getOkHttpFuncString
    }

    private fun createInterceptorAndAddHeader(
        addHeaderState: AddHeaderService.State,
        project: Project
    ): String {
        var interceptorString = ".apply {\n" +
                "            addInterceptor(\n" +
                "                okhttp3.Interceptor { chain ->\n" +
                "                    val builder = chain.request().newBuilder()\n" +
                "                    builder.header(\"${addHeaderState.headerName}\"," +
                "                    \"${addHeaderState.headerValue}\")\n"
        addHeaderState.headerName = ""
        addHeaderState.headerValue = ""
        var wasDialogOK = AddHeaderDialog(
            project
        ).showAndGet()
        while (wasDialogOK) {
            interceptorString += addHeader(addHeaderState)
            wasDialogOK = AddHeaderDialog(
                project
            ).showAndGet()
        }
        interceptorString += "return@Interceptor chain.proceed(builder.build())\n" +
                "                }\n" +
                "            )\n" +
                "        }"
        return interceptorString
    }

    private fun addHeader(addHeaderState: AddHeaderService.State): String {
        val headerString = "builder.header(\"${addHeaderState.headerName}\"," +
                "\"${addHeaderState.headerValue}\")\n"
        addHeaderState.headerName = ""
        addHeaderState.headerValue = ""
        return headerString
    }

    private fun getCallAdapterFactoryString(): String {
        return ".addCallAdapterFactory(retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory.create())\n"
    }
}