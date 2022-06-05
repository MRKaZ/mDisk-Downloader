package com.mrkazofficial.mdiskdownloader.utils

import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.mrkazofficial.mdiskdownloader.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.pow


/**
 * @Project mDisk Downloader
 * @Class Utils
 * @Author MRKaZ
 * @Since 11:14 PM, 5/24/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

object Utils {

    val String.videoId: String
        get() = this.substringAfterLast("/")

    fun Context.isInstalled(intent: Intent): Boolean {
        return this.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .isNotEmpty()
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard: ClipboardManager? =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData
            .newPlainText(null, text)
        clipboard?.setPrimaryClip(clip)
    }

    fun pasteFromClipboard(context: Context): String? {
        val clipboard =
            context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        return if (clipboard.hasPrimaryClip() && (clipboard.primaryClipDescription!!.hasMimeType(
                ClipDescription.MIMETYPE_TEXT_PLAIN
            ) || clipboard.primaryClipDescription!!
                .hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML))
        ) {
            if (clipboard.hasPrimaryClip()) {
                val item = clipboard.primaryClip!!.getItemAt(0)
                item.text.toString()
            } else null
        } else null
    }

    fun Int.formatDurationInt(context: Context): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60

        val sb = StringBuilder()

        if (hours != 0)
            sb.append(context.getString(R.string.time_hours, hours.toString()))

        if (minutes != 0)
            sb.append(context.getString(R.string.time_minutes, minutes.toString()))

        if (seconds != 0)
            sb.append(context.getString(R.string.time_seconds, seconds.toString()))

        return sb.toString()
    }

    fun Long.formatDurationLong(context: Context): String {
        val seconds = this / 1000 % 60
        val minutes = this / 60000 % 60
        val hours = this / 3600000

        val sb = StringBuilder()

        if (hours != 0L)
            sb.append(context.getString(R.string.time_hours, hours.toString()))

        if (minutes != 0L)
            sb.append(context.getString(R.string.time_minutes, minutes.toString()))

        if (seconds != 0L)
            sb.append(context.getString(R.string.time_seconds, seconds.toString()))

        return sb.toString()
    }


    val Long.formatToHumanReadable: String
        get() = kotlin.math.log10(toDouble()).toInt().div(3).let {
            val precision = when (it) {
                0 -> 0; else -> 1
            }
            val suffix = arrayOf("", "Kb", "Mb", "Gb", "Tb", "Pb", "Eb", "Zb", "Yb")
            String.format("%.${precision}f ${suffix[it]}", toDouble() / 10.0.pow(it * 3))
        }

    val String.replaceIllegalCharacters: String
        get() = this.replace("[^A-Za-z0-9 ]".toRegex(), ".")

    fun openFromViaTelegram(context: Context, userId: String): Intent {
        val intent: Intent? = try {
            try {
                context.packageManager.getPackageInfo("org.telegram.messenger", 0) // Telegram
            } catch (e: Exception) {
                context.packageManager.getPackageInfo("org.thunderdog.challegram", 0) // Telegram X
            }
            Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$userId"))
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse("http://www.telegram.me/$userId"))
        }
        return intent!!
    }

    suspend fun retrieveVideoFrameFromVideo(url: String?): Bitmap? {
        var bitmap: Bitmap? = null
        withContext(Dispatchers.IO) {
            var mediaMetadataRetriever: MediaMetadataRetriever? = null
            try {
                if (mediaMetadataRetriever == null)
                    mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(url, HashMap<String, String>())
                bitmap = mediaMetadataRetriever.frameAtTime
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mediaMetadataRetriever?.release()
            }
        }
        return bitmap
    }

    fun File.retrieveFileDuration(context: Context): Long {
        if (!exists()) return 0
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.parse(absolutePath))
        val duration = retriever.extractMetadata(METADATA_KEY_DURATION)
        retriever.release()

        return duration?.toLongOrNull() ?: 0
    }

    fun Context.openGithubProfile() {
        val gitIntent = Intent()
        gitIntent.action = Intent.ACTION_VIEW
        gitIntent.data = Uri.parse("https://github.com/MRKaZ")
        this.startActivity(gitIntent)
    }

    fun Context.openGithubSource() {
        val gitIntent = Intent()
        gitIntent.action = Intent.ACTION_VIEW
        gitIntent.data = Uri.parse("https://github.com/MRKaZ/mDisk-Downloader")
        this.startActivity(gitIntent)
    }
}