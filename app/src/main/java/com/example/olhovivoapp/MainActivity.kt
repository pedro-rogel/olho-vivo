package com.example.olhovivoapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.JavaNetCookieJar
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

class MainActivity : AppCompatActivity() {

    private val TOKEN_SPTRANS = "588f2c03fc4f45e363fcd3e24b03f60c9b9ec9266c6591683afd6c90074d5452"
    private val BASE_URL = "https://api.olhovivo.sptrans.com.br/v2.1"
    private lateinit var tvOutput: TextView

    private val client: OkHttpClient by lazy {
        val cookieManager = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }
        OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // usa seu layout com o TextView
        setContentView(R.layout.activity_main)
        tvOutput = findViewById(R.id.tvOutput)

        // inicia a autenticação
        autenticar { sucesso ->
            runOnUiThread {
                if (sucesso) {
                    tvOutput.text = "Autenticado! Buscando linhas...\n"
                    buscarLinhas("8000")
                } else {
                    tvOutput.text = "Falha na autenticação"
                }
            }
        }
    }

    private fun autenticar(callback: (Boolean) -> Unit) {
        val url = "$BASE_URL/Login/Autenticar?token=$TOKEN_SPTRANS"
        val emptyBody = ByteArray(0).toRequestBody(null)
        val request = Request.Builder()
            .url(url)
            .post(emptyBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OlhoVivo", "Erro no login: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val body = it.body?.string()?.trim().orEmpty()
                    Log.d("OlhoVivo", "Login: $body")
                    callback(body == "true")
                }
            }
        })
    }

    private fun buscarLinhas(termos: String) {
        val url = "$BASE_URL/Linha/Buscar?termosBusca=$termos"
        val request = Request.Builder().url(url).get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OlhoVivo", "Erro buscarLinhas: ${e.message}")
                runOnUiThread {
                    tvOutput.append("\nErro ao buscar linhas: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val json = it.body?.string().orEmpty()
                    Log.i("OlhoVivo", "Linhas: $json")
                    runOnUiThread {
                        // mostra o JSON bruto (ou você pode formatar)
                        tvOutput.append("\nLinhas encontradas:\n$json")
                    }
                }
            }
        })
    }
}
