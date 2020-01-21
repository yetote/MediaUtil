package com.yetote.mediautil.glsl.objects;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.yetote.mediautil.glsl.data.VertexArray;
import com.yetote.mediautil.glsl.programs.RectProgra;
import com.yetote.mediautil.glsl.programs.YUVProgram;

import java.util.Arrays;

public class YUVObj {
    public static final int POSITION_COMPONENT_COUNT = 2;
    public static final int TEXTURE_COMPONENT_COUNT = 2;
    public static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * 4;
    private static final String TAG = "YUVObj";
    private final float[] vertexData = new float[]{
            1.0f, 1.0f, 1f, 0f,
            -1.0f, 1.0f, 0f, 0f,
            -1.0f, -1.0f, 0f, 1f,

            -1.0f, -1.0f, 0f, 1f,
            1.0f, -1.0f, 1f, 1f,
            1.0f, 1.0f, 1f, 0f
    };
    private VertexArray vertexArray;

    public YUVObj() {
        vertexArray = new VertexArray(vertexData);
    }

    public void bindData(YUVProgram program) {
        vertexArray.setVertexAttributePointer(0, program.getAttrPositionLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexArray.setVertexAttributePointer(POSITION_COMPONENT_COUNT, program.getAttrTexCoordLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
    }

    public void changeRatio(float changeH) {
//        int index = 0;
//        while (index < vertexData.length) {
//            vertexData[++index] *= changeH;
//            index += 3;
//        }
        Log.e(TAG, "changeRatio: " + Arrays.toString(vertexData));
    }

    public void draw() {
        GLES30.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}
