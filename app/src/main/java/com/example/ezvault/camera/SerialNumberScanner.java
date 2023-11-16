package com.example.ezvault.camera;

import android.graphics.Bitmap;

import com.example.ezvault.camera.utils.AnyMatcher;
import com.example.ezvault.camera.utils.CrudeMatcher;
import com.example.ezvault.camera.utils.SerialNumberMatcher;
import com.example.ezvault.utils.TaskUtils;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;

public class SerialNumberScanner {
    private final TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    private InputImage inputImage;
    private SerialNumberMatcher matcher;

    /**
     * Create a serial number scanner with a default matcher.
     * @param bitmap The bitmap to use.
     * @param rotation The rotation of the bitmap to use.
     */
    public SerialNumberScanner(Bitmap bitmap, int rotation) {
        this(InputImage.fromBitmap(bitmap, rotation));
    }

    /**
     * Create a serial number scanner with a default matcher.
     * @param image The {@code InputImage} to use.
     */
    public SerialNumberScanner(InputImage image) {
        this(image, new AnyMatcher(new CrudeMatcher()));
    }

    /**
     * Create a serial number scanner.
     * @param image The {@code InputImage} to use.
     * @param matcher The matcher to use.
     */
    public SerialNumberScanner(InputImage image, SerialNumberMatcher matcher) {
        this.inputImage = image;
        this.matcher = matcher;
    }

    /**
     * Process the image for serial numbers.
     * @return A task of a list of possible serial numbers.
     */
    public Task<ArrayList<String>> process() {
        Task<Text> task = recognizer.process(inputImage);
        return TaskUtils.onSuccess(task, text -> {
            ArrayList<String> possibilities = new ArrayList<>(text.getTextBlocks().size());
            for (Text.TextBlock textBlock : text.getTextBlocks()) {
                String contents = textBlock.getText();
                if (matcher.keep(contents)) {
                    possibilities.add(contents);
                }
            }
            return possibilities;
        });
    }
}
