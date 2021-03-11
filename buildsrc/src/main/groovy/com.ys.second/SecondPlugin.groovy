package com.ys.second

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

class SecondPlugin implements Plugin<Project> {

    void apply(Project project) {
        println("========================")
        println("开始加载插件")
        println("========================")

        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new FirstTransform())
        android.registerTransform(new SecondTransform())
    }
}