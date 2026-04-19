#include "string"
#include "iostream"
#include "jni.h"
#include "EncryptDecrypt.h"
#include "com_sis_mtrainer_server_clsCrypto.h"
#include "TokenGenerater.h"
#include "malloc.h"

using namespace std;


JNIEXPORT jbyteArray JNICALL
Java_com_sisindia_ai_mtrainer_android_features_server_clsCrypto_Encrypt(JNIEnv *env, jobject thiz, jbyteArray data) {
    EncryptDecrypt obj;

    jbyte *tempPointer = env->GetByteArrayElements(data, 0);
    const char *cinput = (const char *) tempPointer;
    int dataSize = (int) env->GetArrayLength(data);

    byte *input = new byte[16];
    byte output[16] = {0};

    memcpy(input, tempPointer, dataSize * sizeof(jbyte));

    obj.Encrypt(input, output);

    jbyteArray result = (env)->NewByteArray(dataSize);
    (env)->SetByteArrayRegion(result, 0, dataSize, (jbyte *) output);

    return result;
}

JNIEXPORT jbyteArray JNICALL
Java_com_sisindia_ai_mtrainer_android_features_server_clsCrypto_Decrypt(JNIEnv *env, jobject thiz, jbyteArray data,
                                               jstring key) {
    EncryptDecrypt obj;
    Token token;

    jbyte *tempPointer = env->GetByteArrayElements(data, 0);
    //	const char* cinput = (const char*) tempPointer;
    int dataSize = (int) env->GetArrayLength(data);

   /* byte *input = new byte[dataSize];
    byte *output = new byte[dataSize];*/
    //byte output[16] ={ 0 };
    byte  *input;
    byte *output;

    input = (byte *)calloc(dataSize, sizeof(byte));
    output = (byte *)calloc(dataSize, sizeof(byte));

    memcpy(input, tempPointer, dataSize * sizeof(jbyte));

    jboolean blnIsCopy;
    const char *strA = (env)->GetStringUTFChars(key, &blnIsCopy);
    string decKey = (string)strA;
   // string key1 = token.GeneratePrivateToken(strA);
    obj.Decrypt(input, output, dataSize, decKey);

    jbyteArray result = (env)->NewByteArray(dataSize);
    (env)->SetByteArrayRegion(result, 0, dataSize, (jbyte *) output);

    (env)->ReleaseByteArrayElements(data, tempPointer, 0);
    /*delete(input);
    delete(output);*/
    free(input);
    free(output);
    return result;
}

JNIEXPORT jstring JNICALL
Java_com_sisindia_ai_mtrainer_android_features_server_clsCrypto_CEncryptString(JNIEnv *env, jobject thiz, jstring data) {
//	EncryptDecrypt obj;
//	obj.Encrypt(data,data);
//	const char *str = (*env)->GetStringUTFChars(env, data, 0);
//	(*env)->ReleaseStringUTFChars(env, data, str);
//	string p =obj.EncryptString(str);
    //const char*  p ="enc";
    //return (*env)->NewStringUTF(env, p.c_str());

}

JNIEXPORT jstring JNICALL
Java_com_sisindia_ai_mtrainer_android_features_server_clsCrypto_CDecryptString(JNIEnv *env, jobject thiz, jstring data) {
//	EncryptDecrypt obj;
//	const char *str= (*env)->GetStringUTFChars(env,data,0);
//	string  p =obj.DecryptString(str);
//	char*  p ="dec";
//	return (*env)->NewStringUTF(env,p);
    EncryptDecrypt obj;
    jboolean blnIsCopy;
    const char *strA = (env)->GetStringUTFChars(data, &blnIsCopy);
    string decKey = strA;
    string res = obj.DecryptFiletoString(decKey);
    strA = res.c_str();
    jstring check = (env)->NewStringUTF(strA);
    return check;

}
