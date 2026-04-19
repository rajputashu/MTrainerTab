package com.sisindia.ai.mtrainer.android.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.sisindia.ai.mtrainer.android.db.entities.DraftSpiPhotoEntity
import com.sisindia.ai.mtrainer.android.db.entities.MountedSpiPhotoEntity
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiImage
import okhttp3.internal.checkOffsetAndCount

class SpiUtils {

    companion object {

        @JvmStatic
        fun getGroupedData(imageList: List<SpiImage>): Map<String, List<SpiImage>> {
            return imageList.groupBy { it.uniqueId }
        }

        @JvmStatic
        fun getGroupedDataForSyncing(imageList: List<DraftSpiPhotoEntity>): Map<Int, List<DraftSpiPhotoEntity>> {
            return imageList.groupBy { it.postId }
        }
        @JvmStatic
        fun getGroupedMountDataForSyncing(imageList: List<MountedSpiPhotoEntity>): Map<Int, List<MountedSpiPhotoEntity>> {
            return imageList.groupBy { it.postId }
        }
    }
}