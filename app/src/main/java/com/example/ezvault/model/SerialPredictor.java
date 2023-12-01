package com.example.ezvault.model;

import android.graphics.Bitmap;

import com.example.ezvault.utils.TaskUtils;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.List;

public class SerialPredictor {
    private Task<List<SerialPrediction>> predictText(Bitmap bmp, int rotation) {
        TextRecognizer textRecognizer;
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bmp, rotation);
        Task<Text> textTask = textRecognizer.process(image);
        return TaskUtils.onSuccess(textTask, text -> {
            List<SerialPrediction> predictions = new ArrayList<>();
            for (Text.TextBlock block : text.getTextBlocks()) {
                for (Text.Line line : block.getLines()) {
                    predictions.add(new SerialPrediction(line.getText(), line.getConfidence()));
                }

            }
            return predictions;
        });
    }

    public Task<List<SerialPrediction>> predict(Bitmap bmp, int rotation) {
        return predictText(bmp, rotation);
    }
}
