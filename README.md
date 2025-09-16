# 🚀 Prompt Horizon

**Prompt Horizon** é um plugin para o IntelliJ IDEA que transforma sua IDE em um assistente de geração de prompts. Em vez de pedir para uma IA local modificar seu código (um processo lento e inconsistente), ele a utiliza para uma tarefa mais inteligente: analisar seu código e, junto com suas instruções, gerar um **prompt otimizado e rico em contexto** para ser usado nas mais avançadas IAs externas (como GPT-4, Claude 3, Gemini Advanced, etc.).

Este plugin é a ponte entre seu ambiente de desenvolvimento local e o poder dos grandes modelos de linguagem, economizando tempo e aumentando drasticamente a qualidade das respostas que você obtém.

---

## ✨ Funcionalidades Principais

*   **Geração Rápida de Prompts:** Utiliza um modelo de IA local leve (como Llama 3 8B via Ollama) para analisar o código e gerar prompts.
*   **Contexto de Múltiplos Arquivos:** Selecione quantos arquivos precisar; o plugin os incluirá de forma organizada no prompt final.
*   **Instruções Diretas:** Uma interface simples permite que você dê instruções específicas em linguagem natural, que são integradas diretamente na tarefa principal do prompt.
*   **Otimizado para IAs de Ponta:** O prompt gerado é formatado em Markdown, com seções claras para `### Tarefa`, `### Pontos-Chave para Análise` e `### Contexto do Código Completo`, garantindo a máxima compreensão pela IA externa.
*   **Pronto para Usar:** O prompt final é automaticamente copiado para sua área de transferência e exibido em uma janela para revisão.

---

## 🛠️ Arquitetura

Este plugin foi construído visando eficiência e simplicidade:

1.  **Frontend (IntelliJ Plugin - Kotlin):**
    *   Interface para selecionar arquivos e fornecer instruções.
    *   Faz a chamada para o servidor de IA local.
    *   Recebe a análise da IA.
    *   Monta o prompt final em Markdown.
    *   Exibe o resultado e o copia para a área de transferência.

2.  **Backend (Servidor LLM Local - [Ollama](https://ollama.com/)):**
    *   Expõe uma API REST simples.
    *   Utiliza um modelo de linguagem leve (ex: `llama3:8b`) para realizar a tarefa rápida de analisar o código e gerar um resumo técnico.

Este design desacoplado permite que o "cérebro" da IA seja facilmente trocado sem alterar o código do plugin, usando a IA local para o que ela faz de melhor: pré-processamento rápido de contexto.

---

## ⚙️ Instalação e Configuração

Siga estes passos para ter o Prompt Horizon funcionando em sua máquina.

### Pré-requisitos

1.  **[Ollama](https://ollama.com/) instalado:** O motor que roda a IA local. Siga o guia de instalação oficial.
2.  **Um Modelo de IA Baixado:** É necessário um modelo para a análise. Recomendo o `llama3:8b` por seu equilíbrio entre velocidade e desempenho.
    ```bash
    ollama pull llama3:8b
    ```
3.  **IntelliJ IDEA:** Versão 2023.x ou mais recente (compatível com plugins compilados em JDK 17+).

### Instalação do Plugin

#### Opção A: Baixar o `.zip` (Releases)

1.  Aperte aqui [prompt-horizon-1.0-SNAPSHOT.zip](https://github.com/isaquemsf/prompt-horizon/releases).
2.  Baixe o arquivo `prompt-horizon-x.x.x.zip` .
3.  Abra o IntelliJ IDEA.
4.  Vá para `File` -> `Settings` -> `Plugins`.
5.  Clique no ícone de engrenagem (⚙️) e selecione **`Install Plugin from Disk...`**.
6.  Escolha o arquivo `.zip` que você baixou.
7.  Reinicie a IDE quando solicitado.

#### Opção B: Construir a partir do Código-Fonte

Se você quiser modificar ou construir a versão mais recente diretamente, siga estes passos:

1.  Clone este repositório:
    ```bash
    git clone https://github.com/isaquemsf/prompt-horizon.git
    ```
2.  Abra o projeto clonado no IntelliJ IDEA.
3.  Espere o Gradle sincronizar todas as dependências.
4.  Abra a aba do Gradle no canto direito e execute a tarefa `Tasks -> intellij -> buildPlugin`.
5.  O `.zip` instalável será gerado em `build/distributions/`.
6.  Siga os passos da **Opção A** para instalar o arquivo gerado.

---

## 🚀 Como Usar

1.  **Certifique-se de que o Ollama está rodando** em segundo plano no seu sistema.
2.  No IntelliJ, no painel de projetos, **selecione um ou mais arquivos** que você deseja analisar.
3.  **Clique com o botão direito** nos arquivos selecionados.
4.  No menu de contexto, clique em **`Enviar para IA (Prompt Horizon)`**.
5.  Uma janela aparecerá para você **digitar suas instruções**. Por exemplo:
    *   `Refatore este código para usar o padrão Builder.`
    *   `Encontre possíveis bugs de NullPointerException e sugira correções.`
    *   `Traduza os comentários deste código para o inglês.`
6.  Clique em **`OK`**.
7.  Após uma breve análise, uma janela aparecerá com o **prompt em Markdown completo**. Ele já foi **copiado para sua área de transferência**.
8.  Cole o prompt na sua IA externa preferida e aproveite os resultados de alta qualidade!
