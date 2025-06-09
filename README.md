**OlhoVivoApp**

Este projeto demonstra como consumir a API Olho Vivo da SPTrans no Android, em Kotlin, usando OkHttp e Gson para formatação de JSON.

---

## 1. Como utilizar / como funciona

1. **Pré-requisitos**:
   - Android Studio configurado.
   - Emulador ou dispositivo Android com acesso à internet.
   - Permissão de Internet declarada no `AndroidManifest.xml`:
     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```
2. **Dependências** (no `build.gradle.kts` do módulo `app`):
   ```kotlin
   implementation("com.squareup.okhttp3:okhttp:4.10.0")
   implementation("com.squareup.okhttp3:okhttp-urlconnection:4.10.0")
   // Para formatação "pretty" de JSON
   implementation("com.google.code.gson:gson:2.10.1")
   ```
3. **Layout** (em `res/layout/activity_main.xml`):
   - Crie um `ScrollView` contendo um `TextView` com ID `tvOutput` para exibir o JSON formatado.
4. **Execução**:
   - Abra o projeto no Android Studio.
   - Faça **Sync Project with Gradle Files** (Ctrl+Shift+A → digite “Sync Project”).
   - Inicie seu emulador ou conecte dispositivo físico.
   - Clique em **Run ▶️**.
   - O app irá autenticar, buscar as linhas e exibir o JSON formatado na tela.

---

## 2. Token utilizado

Neste exemplo, o token de acesso configurado em `MainActivity.kt` é:

```kotlin
private val TOKEN_SPTRANS = "588f2c03fc4f45e363fcd3e24b03f60c9b9ec9266c6591683afd6c90074d5452"
```

> **Importante**: este token é fixo e foi gerado na área **Meus Aplicativos** do portal SPTrans. Caso seja regenerado ou removido no portal, você deve atualizar este valor no código.

---

## 3. Requisição feita e finalidade do código

1. **Autenticação**:
   - Endpoint: `POST https://api.olhovivo.sptrans.com.br/v2.1/Login/Autenticar?token={TOKEN}`
   - Finalidade: validação do token e criação de cookie de sessão.
   - No código: método `autenticar { }` que envia um corpo vazio e verifica se o retorno é `true`.
2. **Consulta de linhas**:
   - Endpoint: `GET https://api.olhovivo.sptrans.com.br/v2.1/Linha/Buscar?termosBusca=8000`
   - Finalidade: retorna todas as linhas cujo número ou descrição contenha "8000".
   - No código: método `buscarLinhas("8000")` que obtém o JSON bruto e formata usando Gson.

---

## 4. Formatação de JSON com Gson

Para exibir o JSON de forma legível (com quebras de linha e indentação), o `MainActivity.kt` inclui:

```kotlin
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

// ... dentro de buscarLinhas:
val rawJson = it.body?.string().orEmpty()
val element = JsonParser.parseString(rawJson)
val prettyJson = GsonBuilder()
    .setPrettyPrinting()
    .create()
    .toJson(element)

runOnUiThread {
    tvOutput.text = prettyJson
}
```

Esse trecho utiliza o **JsonParser** para transformar a `String` em elemento JSON, e o **GsonBuilder** com `setPrettyPrinting()` para gerar texto indentado.

---

**Autor**: Pedro Henrique Rogel Salgado  
**Data**: 08/06/2025
