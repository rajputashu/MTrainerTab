/*
 * Copyright (C) 2009 The Android Open Source Project
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
 *
 */
#include <string.h>
#include <jni.h>
//#include "EncryptDecrypt.h"
#include "com_sis_mtrainer_server_clsCrypto.h"


JNIEXPORT jstring JNICALL Java_com_sisindia_ai_mtrainer_android_features_server_clsCrypto_stringFromJNI( JNIEnv* env,jobject thiz )
{
	//getExternalStorageDirectory();
	char* fileName = "test the file";
	char*  p ="hello"; //DecryptFiletoStrings(fileName);
    return (*env)->NewStringUTF(env, p);
}

//JNIEXPORT jstring JNICALL Java_com_trinfin_cryptolibrary_clsCrypto_CEncryptString( JNIEnv* env,jobject thiz,jstring data )
//{
	//EncryptString obj;
	//return obj.EncryptString(data);
	//char*  p ="enc";
	 //return (*env)->NewStringUTF(env, p);
//}

//JNIEXPORT jstring JNICALL Java_com_trinfin_cryptolibrary_clsCrypto_CDecryptString( JNIEnv* env,jobject thiz,jstring data )
//{
//	EncryptString obj;
	//return obj.DecryptString(data);
	//char*  p ="dec";
	//return (*env)->NewStringUTF(env,p);
//}

