package com.example.com.database

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class TableInfo(
    val table_name: String,
    val table_schema: String
)

object SupabaseConfig {
    private const val SUPABASE_URL = "https://pnukuxgecmgeeeggjecw.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBudWt1eGdlY21nZWVlZ2dqZWN3Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0MjYwOTYxMiwiZXhwIjoyMDU4MTg1NjEyfQ.uIQv6rFWmZd1aahzP_SCQ1wcV9nCFsy4hJ8DQpDCw04"

    private val supabase = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
    }

    fun testConnection() {
        runBlocking {
            try {

                val tables = supabase.postgrest.rpc("get_public_tables")
                    .decodeList<TableInfo>()

                println("Successfully connected to Supabase!")
                println("Tables in public schema:")
                tables.forEach { table ->
                    println("- ${table.table_name}")
                }
            } catch (e: Exception) {
                println("Failed to connect to Supabase: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
