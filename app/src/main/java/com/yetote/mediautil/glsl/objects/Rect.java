package com.yetote.mediautil.glsl.objects;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.yetote.mediautil.glsl.data.VertexArray;
import com.yetote.mediautil.glsl.programs.RectProgra;

public class Rect {
    public static final int POSITION_COMPONENT_COUNT = 2;
    public static final int TEXTURE_COMPONENT_COUNT = 2;
    public static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * 4;
    private final float[] vertexData = new float[]{
            0.5f, 0.5f, 1f, 0f,
            -0.5f, 0.5f, 0f, 0f,
            -0.5f, -0.5f, 0f, 1f,

            -0.5f, -0.5f, 0f, 1f,
            0.5f, -0.5f, 1f, 1f,
            0.5f, 0.5f, 1f, 0f
    };
    private VertexArray vertexArray;

    public Rect() {
        vertexArray = new VertexArray(vertexData);
    }

    public void bindData(RectProgra program) {
        vertexArray.setVertexAttributePointer(0, program.getAttrPositionLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexArray.setVertexAttributePointer(POSITION_COMPONENT_COUNT, program.getAttrTexCoordLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        GLES30.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}
