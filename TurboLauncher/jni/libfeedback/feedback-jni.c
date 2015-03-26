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
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <inttypes.h>
#include <sys/stat.h>
#include <dirent.h>
#include <unistd.h>
#include <ctype.h>
#include <assert.h>
#include <fcntl.h>
#include <errno.h>
#include <utime.h> 
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <linux/limits.h>
#include <linux/types.h>
#include <android/log.h>
#include <sys/system_properties.h>
#include <sys/inotify.h>
#include <signal.h>

#define  LOG_TAG    "cooee_apkrm"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

const char cooeeApk[] = "/data/data/com.cooeeui.brand.turbolauncher";

#define cooee_debug

void apkrmNotify(const char * apkpath, char * apkid)
{
    int fd = -1, wd = -1;
    void *p_buf = malloc(sizeof(struct inotify_event));
    if (p_buf == NULL) {
    #ifdef cooee_debug		
        LOGD("malloc failed...");
    #endif        
        goto exit;
    }
    while(1){
        fd = inotify_init();
        if (fd < 0){
    #ifdef cooee_debug
           LOGD("inotify_init failed...");
    #endif        
           goto exit;
        }

        wd = inotify_add_watch(fd, apkpath, IN_DELETE);
        if (wd < 0) {
        #ifdef cooee_debug		
            LOGD("inotify_add_watch failed...");
        #endif        
            goto exit;
        }

    #ifdef cooee_debug    
        LOGD("apkrmnotify observering!\n\r");
    #endif    
        size_t len = read (fd, p_buf, sizeof(struct inotify_event));
        sleep(1);
        DIR* dir = opendir(apkpath);
        if(dir == NULL){
        #ifdef cooee_debug    
            LOGD("cooee turbo apk removing!\n\r");
        #endif
            break;
        }else{
            int fd_pid = open("/data/data/com.cooeeui.brand.turbolauncher/pid", O_RDONLY);
            if (fd_pid < 0){
            #ifdef cooee_debug    
                LOGD("launcher need restart, exit!\n\r");
            #endif
                //bug, if clear the the laucher data, this monitor process exit, then uninstall the launcher apk,
                //the feedback page can not stared, this issue caused by htc phone, if the monitoring process exsiting,
                //the launcher can not started, may due to the monitoring process name same of launcher, so may need 
                //to change the monitoring process name
                goto exit;
            }
            closedir(dir);
            inotify_rm_watch(fd, wd);
            close(fd);
            continue;
        }
    }

    #ifdef cooee_debug    
    LOGD("apkid = %s!\r\n", apkid);
    #endif    
    char str_buf[10];    
    __system_property_get("ro.build.version.sdk", str_buf);
    char *endptr;
    int sdk_ver = strtol(str_buf, &endptr, 0);
    #ifdef cooee_debug    
    LOGD("ro.build.version.sdk = %d", sdk_ver);
    #endif    
    if (strcmp("f566", apkid) == 0){
        if (sdk_ver > 17) //above 4.1
           system("am start --user 0 -a android.intent.action.VIEW -d http://www.turboui.cn/userfeedback.php");
        else
           system("am start -a android.intent.action.VIEW -d http://www.turboui.cn/userfeedback.php");
    #ifdef cooee_debug           
        LOGD("apk cn removed!\n\r");
    #endif        
    }else{
	if (sdk_ver > 17) //above 4.1
            system("am start --user 0 -a android.intent.action.VIEW -d http://www.turboui.cn/userfeedbacken.php");
            //execlp( "am", "am", "start", "--user", "0", "-a", "android.intent.action.VIEW", "-d", "http://www.turboui.cn/userfeedback.php", (char *)NULL);
        else
            system("am start -a android.intent.action.VIEW -d http://www.turboui.cn/userfeedbacken.php");
            //execlp( "am", "am", "start", "-a", "android.intent.action.VIEW", "-d", "http://www.turboui.cn/userfeedback.php", (char *)NULL);
    #ifdef cooee_debug            
        LOGD("apk en removed!\n\r");
    #endif        
    }    
exit:
    if(p_buf != NULL)
        free(p_buf);
    if(wd >= 0)
        inotify_rm_watch(fd, wd);
    if(fd >= 0)
        close(fd);
}


static int call_flg_once = 0;
static pid_t pid_2nd;


/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/HelloJni/HelloJni.java
 */
 //com.iLoong.launcher.desktop
JNIEXPORT jstring Java_com_iLoong_launcher_desktop_feedbackjni_startService( JNIEnv* env, jobject thiz, jstring appId )
{
    char * str_dst = "Hi hacker, service started!";
    call_flg_once++;
    /*if the feedback service started, just return to void start again*/
    int fd_pid = open("/data/data/com.cooeeui.brand.turbolauncher/pid", O_RDONLY);
    if(fd_pid >= 0){
        LOGD("service already started!\r\n");
        return (*env)->NewStringUTF(env, str_dst);
    }

    #ifdef cooee_debug
    LOGD("fork a new process apkrm!\r\n");
    #endif
    int fpid=fork();
	
    if( fpid != 0 ) {
    #ifdef cooee_debug		
        LOGD("parent return!\r\n");
    #endif	    
        return (*env)->NewStringUTF(env, str_dst);//parent
    }

    #ifdef cooee_debug
    LOGD("first children forked!\r\n");
    #endif	
    if(setsid() == -1)
    {
    #ifdef cooee_debug		
       LOGD("setsid failed\n");
    #endif       
       assert(0);
       exit(-1);
    }
    umask(0);
    
    fpid = fork();
    if( fpid != 0) {
    #ifdef cooee_debug		
        LOGD("1st children exit!\r\n");
    #endif		
        exit(0);
    }
    #ifdef cooee_debug
    LOGD("2nd children forked!\r\n");
    #endif
    pid_2nd = getpid();
    #ifdef cooee_debug
    LOGD("2nd children pid=%d!!!\r\n", pid_2nd);
    #endif    

    char* cmd = malloc(100);
    sprintf(cmd, "echo %d > /data/data/com.cooeeui.brand.turbolauncher/pid", pid_2nd);
    #ifdef cooee_debug
    LOGD("cmd = %s\r\n", cmd);
    #endif    
    system(cmd);
    free(cmd);

    char *apkstr = (*env)->GetStringUTFChars(env, appId, 0);
    apkrmNotify(cooeeApk, apkstr);
    #ifdef cooee_debug	
    LOGD("2nd children exit!\r\n");
    #endif	
    exit(0);
	
}

JNIEXPORT jstring Java_com_iLoong_launcher_desktop_feedbackjni_stopService( JNIEnv* env, jobject thiz)
{
    char * str_dst = "Hi hacker, service stopped!";
    char buf[10];
    memset(buf, 0, 10);
    char *endptr;
    
    #ifdef cooee_debug	
    LOGD("stop service!\r\n");
    #endif	
    if (call_flg_once > 0){
        int fd = open("/data/data/com.cooeeui.brand.turbolauncher/pid", O_RDONLY);
        read(fd, buf, 10);
        pid_2nd = strtol(buf, &endptr,0);
        //kill(pid_2nd, 0);
        //kill(0,0);//kill group
        system("rm /data/data/com.cooeeui.brand.turbolauncher/pid");
        #ifdef cooee_debug	
        LOGD("kill service pid = %d!\r\n", pid_2nd);
        #endif		    
    }
    return (*env)->NewStringUTF(env, str_dst);
}
