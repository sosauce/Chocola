package com.sosauce.chocola.domain

import android.media.audiofx.DynamicsProcessing
import androidx.collection.FloatList
import androidx.collection.floatListOf
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.EqualizerBand
import com.sosauce.chocola.data.models.EqualizerPreset
import com.sosauce.chocola.utils.copyMutate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class EqualizerManager(
    private val userPreferences: UserPreferences
) {
    private var dynamicsProcessing: DynamicsProcessing? = null

    private val _eqBands = MutableStateFlow(
        value = getDefaultBandList()
    )
    val eqBands = _eqBands.asStateFlow()


    private fun getDefaultBandList(): List<EqualizerBand> {
        return FREQUENCIES.map { freq ->
            EqualizerBand(
                frequency = freq,
                gain = 0f
            )
        }
    }



    suspend fun initDynamicsProcessing(audioSessionId: Int) {
        val isEnabled = userPreferences.getIsEqualizerEnabled()
        val savedBandGains = userPreferences.getBandGains()

        val initialBands = FREQUENCIES.mapIndexed { index, freq ->
            val gain = savedBandGains.getOrElse(index) { 0f }
            EqualizerBand(frequency = freq, gain = gain)
        }

        val builder = DynamicsProcessing.Config.Builder(
            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
            2, false, 0, false, 0, true, 10, false
        ).apply {
            val eq = DynamicsProcessing.Eq(true, true, 10)

            initialBands.forEachIndexed { i, band ->
                eq.setBand(i, DynamicsProcessing.EqBand(true, band.frequency, band.gain))
            }

            setPostEqAllChannelsTo(eq)
        }.build()

        _eqBands.update { initialBands }

        dynamicsProcessing = DynamicsProcessing(0, audioSessionId, builder).apply {
            enabled = isEnabled
        }
    }

    fun releaseDynamicsProcessing() {
        dynamicsProcessing?.release()
        dynamicsProcessing = null
    }

//    fun getPresets(): List<EqualizerPreset> {
//
//
//        if (equalizer == null) return emptyList()
//
//        return try {
//            (0 until equalizer!!.numberOfPresets).map { band ->
//                EqualizerPreset(
//                    name = equalizer!!.getPresetName(band.toShort()),
//                    band = band.toShort()
//                )
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emptyList()
//        }
//
//    }

    suspend fun setBandGain(targetFrequency: Float, gain: Float) {
        dynamicsProcessing?.let { dp ->
            val postEq = dp.getPostEqByChannelIndex(0)
            var targetIndex = -1

            for (i in 0 until 10) {
                val band = postEq.getBand(i)
                if (abs(band.cutoffFrequency - targetFrequency) < 1.0f) {
                    targetIndex = i
                    break
                }
            }


            if (targetIndex != -1) {
                val updatedBand = DynamicsProcessing.EqBand(true, targetFrequency, gain)
                dp.setPostEqBandAllChannelsTo(targetIndex, updatedBand)
                _eqBands.update {
                    it.copyMutate {
                        this[targetIndex] = EqualizerBand(targetFrequency, gain)
                    }
                }
            }
            userPreferences.saveBandGains(eqBands.value.fastMap { it.gain })
        }

    }




    fun toggleEqualizer(enable: Boolean) {
        dynamicsProcessing?.enabled = enable
    }


    suspend fun usePreset(preset: FloatList) {
        preset.forEachIndexed { index, gain ->
            val freq = FREQUENCIES[index]
            setBandGain(freq, gain)
        }
    }

    fun getPresets(): List<EqualizerPreset> {
        return listOf(
            EqualizerPreset(name = "Flat", emoji = "⚖️", gains = FLAT),
            EqualizerPreset(name = "Bass Boost", emoji = "🔊", gains = BASS_BOOST),
            EqualizerPreset(name = "Vocal Booster", emoji = "🎙️", gains = VOCAL_BOOSTER),
            EqualizerPreset(name = "Pop", emoji = "🎤", gains = POP),
            EqualizerPreset(name = "Rock", emoji = "🎸", gains = ROCK),
            EqualizerPreset(name = "EDM", emoji = "🪩", gains = EDM)
        )
    }


    companion object {

        val FREQUENCIES = floatArrayOf(
            31f, 63f, 125f, 250f, 500f,
            1000f, 2000f, 4000f, 8000f, 16000f
        )

        val BASS_BOOST = floatListOf(6f, 5f, 4f, 2f, 0f, 0f, 0f, 0f, 0f, 0f)
        val FLAT = floatListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val VOCAL_BOOSTER = floatListOf(-2f, -1f, 0f, 1f, 3f, 4f, 3f, 1f, 0f, -1f)
        val POP = floatListOf(-1f, 1f, 2f, 3f, 2f, -1f, -2f, -1f, 1f, 2f)
        val ROCK = floatListOf(5f, 4f, 2f, 0f, -1f, 0f, 2f, 3f, 4f, 4f)
        val EDM = floatListOf(6f, 5f, 2f, 0f, -2f, 2f, 1f, 3f, 5f, 4f)

    }

}