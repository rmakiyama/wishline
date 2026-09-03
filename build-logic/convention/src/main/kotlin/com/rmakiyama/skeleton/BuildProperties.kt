package com.rmakiyama.skeleton

import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal val Project.warningsAsErrors: Provider<Boolean>
    get() = providers.gradleProperty("warningsAsErrors").map(String::toBoolean).orElse(false)
