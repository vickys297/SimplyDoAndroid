package com.example.simplydo.localDatabase

import androidx.room.TypeConverter
import com.example.simplydo.model.ContactInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConverterHelper {
    val gson = Gson()

    @TypeConverter
    fun fromContactInfoList(list: ArrayList<ContactInfo>): String {
        return gson.toJson(list)
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
    fun toContactInfoList(json: String): ArrayList<ContactInfo> {
        return gson.fromJson(json, object : TypeToken<ArrayList<ContactInfo?>?>() {}.type)
    }
}