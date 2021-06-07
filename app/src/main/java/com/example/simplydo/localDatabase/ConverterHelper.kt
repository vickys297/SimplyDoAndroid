package com.example.simplydo.localDatabase

import androidx.room.TypeConverter
import com.example.simplydo.model.ContactModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConverterHelper {
    private val gson = Gson()

    @TypeConverter
    fun fromContactInfoList(list: ArrayList<ContactModel>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromBoolean(boolean: Boolean): Int {
        return if (boolean) 1 else 0
    }

    @TypeConverter
    fun toBoolean(boolean: Int): Boolean {
        return boolean == 1
    }

    @TypeConverter
    fun fromStringList(list: ArrayList<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String): ArrayList<String> {
        return gson.fromJson(json, object : TypeToken<ArrayList<String?>?>() {}.type)
    }


    @TypeConverter
    fun toContactInfoList(json: String): ArrayList<ContactModel> {
        return gson.fromJson(json, object : TypeToken<ArrayList<ContactModel?>?>() {}.type)
    }
}