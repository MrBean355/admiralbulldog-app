package com.github.mrbean355.bulldog.gsi.triggers

val SoundTriggerType.configKey: String
    get() = requireNotNull(simpleName)