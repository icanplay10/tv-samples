/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tv.reference.shared.datamodel

import com.squareup.moshi.JsonClass

/**
 * Represents a simplified API response
 *
 * The content is always a List of some kind of object and the response can also include
 * an arbitrary Map of Strings. Errors are not represented.
 */
@JsonClass(generateAdapter = true)
class ApiResponse<T>(val content: List<T>, val metadata: Map<String, String>?)