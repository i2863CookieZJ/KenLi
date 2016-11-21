/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.higgses.griffin.imageloader.core.process;

import com.higgses.griffin.imageloader.core.DisplayImageOptions;

import android.graphics.Bitmap;

/**
 * Makes some processing on {@link android.graphics.Bitmap}. Implementations can apply any changes to original {@link android.graphics.Bitmap}.<br />
 * Implementations have to be thread-safe.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
public interface BitmapProcessor {
	/**
	 * Makes some processing of incoming bitmap.<br />
	 * This method is executing on additional thread (not on UI thread).<br />
	 * <b>Note:</b> If this processor is used as {@linkplain DisplayImageOptions.Builder#preProcessor(BitmapProcessor)
	 * pre-processor} then don't forget {@linkplain android.graphics.Bitmap#recycle() to recycle} incoming bitmap if you return a new
	 * created one.
	 *
	 * @param bitmap Original {@linkplain android.graphics.Bitmap bitmap}
	 * @return Processed {@linkplain android.graphics.Bitmap bitmap}
	 */
	Bitmap process(Bitmap bitmap);
}
