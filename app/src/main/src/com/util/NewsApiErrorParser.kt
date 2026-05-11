package com.util

import org.json.JSONObject
import retrofit2.Response

object NewsApiErrorParser {

    fun parseMessage(response: Response<*>): String? {
        return runCatching {
            val raw = response.errorBody()?.string().orEmpty()
            if (raw.isBlank()) return@runCatching null

            val json = JSONObject(raw)
            val code = json.optString("code").orEmpty()
            val message = json.optString("message").orEmpty()

            when {
                code.equals("apiKeyMissing", ignoreCase = true) ->
                    "Thiếu NEWS_API_KEY. Kiểm tra lại file local.properties."

                code.equals("apiKeyInvalid", ignoreCase = true) ->
                    "NEWS_API_KEY không hợp lệ. Kiểm tra lại key NewsAPI."

                code.equals("apiKeyDisabled", ignoreCase = true) ->
                    "NEWS_API_KEY đã bị vô hiệu hóa."

                code.equals("rateLimited", ignoreCase = true) ->
                    "Đã vượt giới hạn NewsAPI. Vui lòng thử lại sau hoặc đổi API key."

                code.equals("sourcesTooMany", ignoreCase = true) ->
                    "Bạn đang chọn quá nhiều nguồn tin cùng lúc."

                code.equals("sourceDoesNotExist", ignoreCase = true) ->
                    "Nguồn tin này không tồn tại trên NewsAPI."

                message.isNotBlank() ->
                    message

                else -> null
            }
        }.getOrNull()
    }
}

