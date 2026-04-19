    **Droid commons module**
        1. **Scalable** density pixel values resources
    ---

    **build.gradle dependencies**
    
    1. Dagger Dependencies
        implementation "com.google.dagger:dagger-android-support:2.26"      //daggerSupport
        kapt "com.google.dagger:dagger-android-processor:2.26"              //daggerProcessor
        kapt "com.google.dagger:dagger-compiler:2.26"                       //daggerCompiler
        implementation "com.google.dagger:dagger:2.26"                      //dagger
    
    2. AndroidX Dependencies
        implementation "androidx.annotation:annotation:1.1.0"               //androidXAnnotations
        implementation "com.google.android.material:material:1.1.0"         //androidMaterial
        implementation "androidx.recyclerview:recyclerview:1.1.0"           //androidXRecyclerview
        implementation "androidx.appcompat:appcompat:1.1.0"                 //androidXRAppcompat
        implementation "androidx.legacy:legacy-support-v13:1.0.0"           //androidXSupportV13
        implementation "androidx.legacy:legacy-support-v4:1.0.0"            //androidXSupportV4
        implementation "androidx.room:room-runtime:2.2.4"                   //androidXRoomRuntime
        implementation "androidx.room:room-rxjava2:2.2.4"                   //androidXRoomRxJava
        implementation "androidx.multidex:multidex:2.0.1"                   //androidXMultidex
        kapt "androidx.room:room-compiler:2.2.4"                            //androidXRoomCompiler
    
    3.Android Life Cycle
        implementation "androidx.lifecycle:lifecycle-common-java8:2.2.0"
        implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
        implementation "androidx.lifecycle:lifecycle-viewmodel:2.2.0"
        implementation "androidx.lifecycle:lifecycle-common-java8:2.2.0"
        //noinspection LifecycleAnnotationProcessorWithJava8
        kapt "androidx.lifecycle:lifecycle-compiler:2.2.0"

    4.Retrofit Dependencies
        implementation "com.squareup.okhttp3:okhttp:4.2.2"                  //okHttp
        implementation "com.squareup.okhttp3:logging-interceptor:3.12.0"    // okHttpLogger
        implementation "com.squareup.retrofit2:retrofit:2.7.0"              //retrofit
        implementation "com.squareup.retrofit2:converter-gson:2.6.1"        //retrofitConverter
        implementation "com.squareup.retrofit2:adapter-rxjava2:2.7.0"       //retrofitAdapter
        implementation "com.squareup.retrofit2:converter-scalars:2.4.0"     //retrofitScalar

    5.Other Dependencies
        implementation "com.jakewharton.timber:timber:4.7.1"                //timber
        implementation "com.tspoon.traceur:traceur:1.0.1"                   //traceur
        implementation "com.facebook.stetho:stetho:1.5.1"                   //stetho
        implementation "com.facebook.stetho:stetho-okhttp3:1.5.1"           //stethoOkhttp

    6. Rx
        implementation "io.reactivex.rxjava2:rxkotlin:2.4.0"                //rxKotlin
        implementation "io.reactivex.rxjava2:rxandroid:2.1.1"               //rxAndroid
        implementation "io.reactivex.rxjava2:rxjava:2.2.16"                 //rxJava
    
    7. Glide
        implementation "com.github.bumptech.glide:glide:4.11.0"              //Glide

    8. JavaX
        implementation "javax.annotation:jsr250-api:1.0"                  //javaxAnnotation
        implementation "javax.inject:javax.inject:1"                      //javaxInject

    9. Gson
        implementation "com.google.code.gson:gson:2.8.5"                    //Gson

    10. PARCELS
        implementation 'org.parceler:parceler-api:1.1.12'
        kapt 'org.parceler:parceler:1.1.12'

    11. THREE-TEN-BP
        implementation "com.jakewharton.threetenabp:threetenabp:1.2.1"

    12.Chuck
        debugImplementation "com.readystatesoftware.chuck:library:1.1.0"//chuckDebug
        releaseImplementation "com.readystatesoftware.chuck:library-no-op:1.0.4"//chuckRelease

 will update soon
 