package com.example.newsapp.data.remote

class ApiException(
    val code: Int,
    message: String
) : Exception(message)