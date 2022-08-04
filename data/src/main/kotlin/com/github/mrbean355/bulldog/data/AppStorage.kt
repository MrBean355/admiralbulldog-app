/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.bulldog.data

import org.apache.commons.lang3.SystemUtils.IS_OS_LINUX
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import org.apache.commons.lang3.SystemUtils.USER_HOME
import java.io.File

private const val AppFolder = "admiralbulldog-app"

internal object AppStorage {
    private val appDataPath = getAppDataPath()
    private val appDirName = getAppDirName()

    fun getFile(name: String): File = File(getRoot(), name)

    private fun getRoot(): File {
        val appData = File(appDataPath)
        require(appData.exists() && appData.isDirectory) {
            "Missing app data folder: ${appData.absolutePath}"
        }
        return File(appData, appDirName)
            .also(File::mkdir)
    }

    private fun getAppDataPath(): String {
        return when {
            IS_OS_WINDOWS -> System.getenv("APPDATA")
            IS_OS_MAC -> "$USER_HOME/Library/Application Support/"
            else -> USER_HOME
        }
    }

    private fun getAppDirName(): String {
        return if (IS_OS_LINUX) ".$AppFolder" else AppFolder
    }
}