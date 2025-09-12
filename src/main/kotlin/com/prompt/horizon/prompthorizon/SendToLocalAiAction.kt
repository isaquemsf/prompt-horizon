package com.prompt.horizon.prompthorizon

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.swing.JComponent
import javax.swing.JPanel

class SendToLocalAiAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        if (virtualFiles.isEmpty()) return

        // Passo 1: Capturar as instruções do usuário (interface familiar)
        val instructionDialog = InstructionDialog(project)
        if (!instructionDialog.showAndGet()) return
        val userInstructions = instructionDialog.getInstructions()

        val filesData = virtualFiles.filter { !it.isDirectory }.map { vf ->
            Pair(vf.path.substringAfterLast("/"), String(vf.contentsToByteArray(), Charsets.UTF_8)) // Guarda só o nome do arquivo
        }
        if (filesData.isEmpty()) return

        // Passo 2: Rodar a tarefa de background que agora é muito mais rápida
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Gerando Prompt Otimizado...") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    // Passo 3: Construir um payload JSON simples
                    val jsonPayload = buildJsonPayloadForAnalysis(filesData, userInstructions)

                    val client = HttpClient.newBuilder().build()
                    val request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434/api/generate"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build()

                    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                    val aiAnalysis = extractRawResponse(response.body())

                    // O plugin agora monta o prompt final
                    val finalPrompt = assembleFinalPrompt(userInstructions, aiAnalysis, filesData)

                    ApplicationManager.getApplication().invokeLater {
                        copyToClipboardAndShowDialog(project, finalPrompt)
                    }

                } catch (ex: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        // Tratar erro de conexão
                    }
                }
            }
        })
    }

    // PROMPT FOCADO - O CORAÇÃO DA NOVA LÓGICA
    // Pede apenas por um resumo/análise.
    private fun buildJsonPayloadForAnalysis(filesData: List<Pair<String, String>>, userInstructions: String): String {
        val fileContext = filesData.joinToString("\n---\n") { (fileName, content) ->
            "// Arquivo: $fileName\n$content"
        }

        val task = userInstructions.ifBlank { "analisar este código para refatoração e melhorias gerais." }

        val promptTemplate = """
            Você é um arquiteto de software. Analise o código-fonte fornecido com base na tarefa do usuário.
            Sua resposta deve ser um parágrafo de texto único, conciso e técnico (em português).
            Este parágrafo deve resumir os pontos mais importantes que uma IA de programação deve focar para completar a tarefa.
            NÃO gere código. NÃO formate em Markdown. Apenas o texto da análise.

            ### Tarefa do Usuário
            $task

            ### Código-Fonte Completo
            $fileContext
        """.trimIndent()

        val escapedPrompt = promptTemplate.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

        // Voltamos para o modelo rápido!
        return """{"model": "llama3:8b", "prompt": "$escapedPrompt", "stream": false}"""
    }

    // NOVO: Esta função, executada pelo nosso plugin, monta o prompt final.
    private fun assembleFinalPrompt(userTask: String, aiAnalysis: String, filesData: List<Pair<String, String>>): String {
        val codeContext = filesData.joinToString("\n\n---\n\n") { (fileName, content) ->
            "### Arquivo: `$fileName`\n```java\n$content\n```"
        }

        val task = userTask.ifBlank { "[ADICIONE SUA TAREFA AQUI, EX: Refatore este código para ser mais eficiente.]" }

        return """
            ### Tarefa Principal
            $task

            ### Pontos-Chave para Análise (sugerido pela IA local)
            $aiAnalysis

            ### Contexto do Código Completo
            $codeContext
        """.trimIndent()
    }

    private fun extractRawResponse(jsonResponse: String): String {
        val responseKey = "\"response\":\""
        val startIndex = jsonResponse.indexOf(responseKey) + responseKey.length
        if (startIndex < responseKey.length) return "Erro: Resposta JSON inválida."

        val endIndex = jsonResponse.lastIndexOf("\"")
        if (endIndex <= startIndex) return "Erro: Resposta JSON inválida."

        return jsonResponse.substring(startIndex, endIndex)
            .replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\")
    }

    private fun copyToClipboardAndShowDialog(project: Project?, generatedPrompt: String) {
        val selection = StringSelection(generatedPrompt)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)

        val textArea = JBTextArea(generatedPrompt, 20, 100).apply { isEditable = false }
        val scrollPane = JBScrollPane(textArea)
        scrollPane.preferredSize = Dimension(900, 600)

        val builder = DialogBuilder(project)
        builder.setCenterPanel(scrollPane)
        builder.setTitle("Prompt Gerado e Copiado para a Área de Transferência")
        builder.addOkAction().setText("Fechar")
        builder.show()
    }
}

class InstructionDialog(project: Project?) : DialogWrapper(project) {
    private val textArea = JBTextArea(10,50)

    init {
        title = "Instruções para a IA"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(BorderLayout())
        textArea.emptyText.text = "Ex: 'Refatore o método X para usar a API de Streams.' ou 'Corrija o bug de divisão por zero.'"
        val scrollPane = JBScrollPane(textArea)
        panel.add(scrollPane, BorderLayout.CENTER)
        panel.preferredSize = Dimension(600,200)
        return panel
    }

    fun getInstructions(): String {
        return textArea.text
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return textArea
    }

}