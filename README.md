# 🚀 Prompt Horizon

**Prompt Horizon** é um plugin para IDEs da JetBrains (IntelliJ IDEA, PyCharm, etc.) que o transforma em um assistente de geração de prompts. Em vez de pedir para uma IA local modificar seu código — um processo lento e propenso a erros — ele a utiliza para uma tarefa mais inteligente: analisar seu código e, junto com suas instruções, gerar um **prompt otimizado e rico em contexto** para ser usado nas mais avançadas IAs externas (como GPT-4, Claude 3 ou Gemini Advanced).

Este plugin é a ponte entre seu ambiente de desenvolvimento local e o poder dos grandes modelos de linguagem, economizando tempo e aumentando drasticamente a qualidade das respostas que você obtém.

---

## ✨ Funcionalidades Principais

*   **Multiplataforma:** Funciona perfeitamente tanto com projetos **Java** no IntelliJ IDEA quanto com projetos **Python** no PyCharm.
*   **Geração Rápida de Prompts:** Utiliza um modelo de IA local leve (via Ollama) para analisar o código e gerar prompts em segundos.
*   **Contexto de Múltiplos Arquivos:** Selecione quantos arquivos precisar; o plugin os incluirá de forma organizada no prompt final, seja `.java` ou `.py`.
*   **Instruções Diretas:** Uma interface simples permite que você dê instruções em linguagem natural, que são integradas diretamente na tarefa principal do prompt.
*   **Otimizado para IAs de Ponta:** O prompt gerado é formatado em Markdown, com seções claras para `### Tarefa Principal`, `### Pontos-Chave para Análise` e `### Contexto do Código Completo`.
*   **Pronto para Usar:** O prompt final é automaticamente copiado para sua área de transferência e exibido em uma janela para revisão.

---

## 🛠️ Arquitetura

Este plugin foi construído visando eficiência e flexibilidade:

1.  **Frontend (Plugin JetBrains - Kotlin):** A interface de usuário que roda dentro do IntelliJ ou PyCharm, responsável por coletar dados, chamar a IA e formatar o prompt final.
2.  **Backend (Servidor LLM Local - [Ollama](https://ollama.com/)):** Roda qualquer modelo de linguagem compatível (recomendado: `llama3:8b`) para realizar a tarefa rápida de analisar o código e gerar um resumo técnico.

Este design desacoplado permite o uso da IA local para o que ela faz de melhor: pré-processamento rápido de contexto.

---

## ⚙️ Instalação e Configuração

Siga estes passos para ter o Prompt Horizon funcionando em sua máquina.

### Pré-requisitos

1.  **[Ollama](https://ollama.com/) instalado:** O motor que roda a IA local. Siga o guia de instalação oficial no site deles.
2.  **Um Modelo de IA Baixado:** É necessário um modelo para a análise. Recomendo o `llama3:8b` por seu excelente equilíbrio entre velocidade e desempenho.
    ```bash
    ollama pull llama3:8b
    ```
3.  **Uma IDE JetBrains:** IntelliJ IDEA ou PyCharm, versão 2023.1 ou mais recente.

### Instalação do Plugin

1.  Vá para a **[página de Releases](https://github.com/isaquemsf/prompt-horizon/releases)** deste repositório.
2.  Baixe o arquivo `.zip` da versão mais recente (ex: `prompt-horizon-1.0.0.zip`).
3.  Abra sua IDE (IntelliJ ou PyCharm).
4.  Vá para `File` -> `Settings` -> `Plugins`.
5.  Clique no ícone de engrenagem (⚙️) e selecione **`Install Plugin from Disk...`**.
6.  Escolha o arquivo `.zip` que você baixou.
7.  Reinicie a IDE quando solicitado.

---

## 🚀 Como Usar

O fluxo de trabalho é idêntico no IntelliJ e no PyCharm:

1.  **Certifique-se de que o Ollama está rodando** em segundo plano no seu sistema.
2.  No painel de projetos, **selecione um ou mais arquivos** que você deseja analisar (`.java`, `.py`, etc.).
3.  **Clique com o botão direito** nos arquivos selecionados.
4.  No menu de contexto, clique em **`Enviar para IA (Prompt Horizon)`**.
5.  Uma janela aparecerá para você **digitar suas instruções**. Por exemplo:
    *   *Para Java:* `Refatore esta classe para usar o padrão Builder.`
    *   *Para Python:* `Converta esta função
