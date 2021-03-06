// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.cureit;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.example.cureit.common.FrameMetadata;
import com.example.cureit.common.GraphicOverlay;
import com.example.cureit.VisionProcessorBase;
import com.example.cureit.add_records;

import java.util.List;

/**
 * Processor for the cloud document text detector demo.
 */
public class CloudDocumentTextRecognitionProcessor
        extends VisionProcessorBase<FirebaseVisionDocumentText> {

    private static final String TAG = "CloudDocTextRecProc";

    private final FirebaseVisionDocumentTextRecognizer detector;
    String record_uid;

    private DatabaseReference mDatabase;
    private String user_id;
    private FirebaseAuth mAuth;
    private FirebaseUser mCureentUser;

    public CloudDocumentTextRecognitionProcessor() {
        super();
        detector = FirebaseVision.getInstance().getCloudDocumentTextRecognizer();

    }

    public void setTemp (String temp, String userid){
        this.record_uid = temp;
        this.user_id = userid;
    }

    @Override
    protected Task<FirebaseVisionDocumentText> detectInImage(FirebaseVisionImage image) {
        mAuth = FirebaseAuth.getInstance();
        mCureentUser = mAuth.getCurrentUser();
        //user_id = mCureentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Records" ).child( user_id ).child( record_uid );
        return detector.processImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull FirebaseVisionDocumentText text,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();


        String temp = text.getText();

        String medName = new String();
        String medTime = new String();
        String medQty = new String();
        int currentIndex=0;
        int tempStart = 0;
        while(currentIndex < temp.length()-2)
        {
            if( Character.isWhitespace(temp.charAt(currentIndex)) && Character.isDigit(temp.charAt(currentIndex+1)))
            {
                medName = medName.concat(temp.substring(tempStart,currentIndex));
                medName = medName.concat("\n");
                medTime = medTime.concat(temp.substring(currentIndex+1,currentIndex+6));
                medTime = medTime.concat("\n");
                medQty = medQty.concat(temp.substring(currentIndex+8,currentIndex+10));
                medQty = medQty.concat("\n");

                currentIndex++;
                tempStart = currentIndex+11;

            }

            currentIndex++;
        }

        String temp1 = "Medicine Name: " + medName + "\n" + "Quantity: " + medQty +"\n" + "----- x -----\n" + text.getText();
        mDatabase.child( "prescription_Text" ).setValue( temp1 );

        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    List<FirebaseVisionDocumentText.Symbol> symbols = words.get(l).getSymbols();
                    for (int m = 0; m < symbols.size(); m++) {
                        //
                    }
                }
            }
        }
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Cloud Document Text detection failed." + e);
    }
}
