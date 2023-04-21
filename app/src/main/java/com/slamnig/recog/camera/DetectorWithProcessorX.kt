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

package com.slamnig.recog.camera

import android.media.Image
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.camera.DetectionTaskCallback
import com.google.mlkit.vision.interfaces.Detector
import java.io.IOException

class DetectorWithProcessorX(
    val detector: Detector<Any>,
    val callback: DetectionTaskCallback<Any>
) {
    fun close() {
        try {
            detector.close()
        } catch (e: IOException) {
        }
    }

    fun process(image: Image, rotationDegrees: Int): Task<Any>
    {
        // The base Detector interface is very poorly documented.
        // There's no mention of the base process() function, but (decompiled)
        // CameraXSource code calls it, and it's public.
        // Since it takes Image as first argument, it's not necessary to convert it to InputImage.
        // The second (undocumented) argument is rotation degrees:
        val task = detector.process(image, rotationDegrees)
        callback.onDetectionTaskReceived(task)
        return task
    }
}