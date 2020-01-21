package com.yetote.mediautil.glsl.programs;

import android.content.Context;
import android.util.Log;

import com.yetote.mediautil.R;

import java.nio.Buffer;

import static android.opengl.GLES20.GL_LUMINANCE;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE2;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glUniform1i;

public class YUVProgram extends ShaderProgram {
    public final int aPositionLocation;
    public final int aTexCoordLocation;
    public final int uTexYLocation;
    public final int uTexULocation;
    public final int uTexVLocation;
    private static final String TAG = "RectProgram";
    private static int[] yuvLocation;
    private static final int[] YUV_TEXTURE_ID = {GL_TEXTURE0, GL_TEXTURE1, GL_TEXTURE2};
    private int width, height;

    public YUVProgram(Context context) {
        super(context, R.raw.yuv_vertex_shader, R.raw.yuv_frag_shader);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTexCoordLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        uTexYLocation = glGetUniformLocation(program, U_TEXY);
        uTexULocation = glGetUniformLocation(program, U_TEXU);
        uTexVLocation = glGetUniformLocation(program, U_TEXV);
        yuvLocation = new int[]{uTexYLocation, uTexULocation, uTexVLocation};
    }

    public int getAttrPositionLocation() {
        return aPositionLocation;
    }

    public int getAttrTexCoordLocation() {
        return aTexCoordLocation;
    }

    public void sexPixel(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void setUniform(int textureId, Buffer buffer, int type) {
        if (width == 0 || height == 0) {
            Log.e(TAG, "setUniform: 未设置分辨率");
            return;
        }
        if (buffer == null) {
            Log.e(TAG, "setUniform: 数据为null，无法加载");
            return;
        }
        buffer.position(0);
        glActiveTexture(GL_TEXTURE0 + type);
        glBindTexture(GL_TEXTURE_2D, textureId);
        if (type == 0) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, buffer);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, buffer);
        }
        glUniform1i(yuvLocation[type], type);
    }
}
