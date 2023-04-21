/*
Copyright 2023. Davor Slamnig

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.slamnig.recog.recog

import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.interfaces.Detector
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.slamnig.recog.*
import com.slamnig.recog.viewmodel.ImageRecogViewModel

/**
 * Still image recognizer - text and barcode.
 */
@Suppress("UNCHECKED_CAST")
class ImageRecog(viewModel: ImageRecogViewModel) : BaseImageRecog(viewModel = viewModel)
{
    private val LOGTAG = this.javaClass.simpleName

    override fun setRecogMode(recogMode: Int)
    {
        super.setRecogMode(recogMode)

        detector = when (mode) {
            RECOG_TEXT ->
                initText()
            RECOG_BARCODE ->
                initBarcode()
            else ->
                null
        }
    }

    private fun initText(): Detector<Any>
    {
        return TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) as Detector<Any>
    }

    private fun initBarcode(): Detector<Any>
    {
        // recognize all types:
        val options = BarcodeScannerOptions.Builder()
            //.setBarcodeFormats(
            //    Barcode.FORMAT_QR_CODE,
            //    Barcode.FORMAT_AZTEC)
            .build()

        return BarcodeScanning.getClient(options) as Detector<Any>
    }

    override fun recog(image: InputImage)
    {
        detector?.apply {
            when(this){
                is TextRecognizer ->
                    process(image)
                        .addOnSuccessListener { text ->
                            viewModel.setText(text)
                        }
                is BarcodeScanner ->
                    process(image)
                        .addOnSuccessListener { barcodes ->
                            barcodes?.let {
                                viewModel.setBarcodes(it.filterNotNull())
                            }
                        }
            }
        }
    }
}