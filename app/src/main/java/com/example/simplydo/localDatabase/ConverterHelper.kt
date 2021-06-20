package com.example.simplydo.localDatabase

import androidx.room.TypeConverter
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.LatLngModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConverterHelper {
    private val gson = Gson()

    //  contactAddress
    //  galleryFiles
    //  audioFiles
    //  documentFiles
    @TypeConverter
    fun fromContactAddressList(list: ArrayList<ContactModel>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toContactAddressList(json: String): ArrayList<ContactModel> {
        return gson.fromJson(json, object : TypeToken<ArrayList<ContactModel?>?>() {}.type)
    }

    @TypeConverter
    fun fromGalleryFilesList(list: ArrayList<GalleryModel>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toGalleryFilesList(json: String): ArrayList<GalleryModel> {
        return gson.fromJson(json, object : TypeToken<ArrayList<GalleryModel?>?>() {}.type)
    }

    @TypeConverter
    fun fromAudioFilesList(list: ArrayList<AudioModel>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toAudioFilesList(json: String): ArrayList<AudioModel> {
        return gson.fromJson(json, object : TypeToken<ArrayList<AudioModel?>?>() {}.type)
    }

    @TypeConverter
    fun fromDocumentFilesList(list: ArrayList<FileModel>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toDocumentFilesList(json: String): ArrayList<FileModel> {
        return gson.fromJson(json, object : TypeToken<ArrayList<FileModel?>?>() {}.type)
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
    fun fromLatLng(latLng: LatLngModel): String {
        return gson.toJson(latLng)
    }

    @TypeConverter
    fun toLatLng(json: String): LatLngModel {
        return gson.fromJson(json, object : TypeToken<LatLngModel?>() {}.type)
    }


}