/*
 * Copyright (C) 2017 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class ObjectInstanceConverterFactory extends Converter.Factory {
  public static final Object VALUE = new Object();

  @Override public @Nullable Converter<ResponseBody, ?> responseBodyConverter(
          @NotNull Type type, @NotNull Annotation[] annotations, @NotNull Retrofit retrofit) {
    if (type != Object.class) {
      return null;
    }
    return new Converter<ResponseBody, Object>() {
      @Override public Object convert(@NotNull ResponseBody value) {
        return VALUE;
      }
    };
  }
}
