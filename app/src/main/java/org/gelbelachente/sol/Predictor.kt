package org.gelbelachente.sol

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import org.gelbelachente.sol.ml.OffshoreModel
import org.gelbelachente.sol.ml.OnshoreModel
import org.gelbelachente.sol.ml.SolarModel
import org.tensorflow.lite.DataType
import java.io.Closeable
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

interface Predictor : Closeable {
    abstract fun predict(input: FloatArray) : Float
}

class OffshorePredictor(val ctx : Context) : Predictor{

    private val model = OffshoreModel.newInstance(ctx)

    override fun predict(input: FloatArray): Float {
        val inputTensor = TensorBuffer.createFixedSize(intArrayOf(1, 6), DataType.FLOAT32)
        inputTensor.loadArray(input)
        val outputs = model.process(inputTensor)
        val output = outputs.outputFeature0AsTensorBuffer
        return output.getFloatValue(0)
    }

    override fun close() {
        model.close()
    }

}

class OnshorePredictor(val ctx : Context) : Predictor{

    private val model = OnshoreModel.newInstance(ctx)

    override fun predict(input: FloatArray): Float {
        val inputTensor = TensorBuffer.createFixedSize(intArrayOf(1, 10), DataType.FLOAT32)
        inputTensor.loadArray(input)
        val outputs = model.process(inputTensor)
        val output = outputs.outputFeature0AsTensorBuffer
        return output.getFloatValue(0)
    }

    override fun close() {
        model.close()
    }

}

class SolarPredictor(val ctx : Context) : Predictor{

    private val model = SolarModel.newInstance(ctx)

    override fun predict(input: FloatArray): Float {
        val inputTensor = TensorBuffer.createFixedSize(intArrayOf(1, 12), DataType.FLOAT32)
        inputTensor.loadArray(input)
        val outputs = model.process(inputTensor)
        val output = outputs.outputFeature0AsTensorBuffer
        return output.getFloatValue(0)
    }

    override fun close() {
        model.close()
    }

}
