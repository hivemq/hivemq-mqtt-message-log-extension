/*
 * Copyright 2018-present HiveMQ GmbH
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
package com.hivemq.extensions.log;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.testcontainers.utility.DockerImageName;

public class DockerImageNames {

    public static final @NotNull DockerImageName HIVEMQ =
            DockerImageName.parse("acidsepp/hivemq-ce").withTag("latest").asCompatibleSubstituteFor("hivemq/hivemq-ce");
}
