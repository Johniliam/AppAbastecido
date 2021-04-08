package com.example.abastecido

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class ApiResponse (
    @SerializedName("message") var root: List<String>
)