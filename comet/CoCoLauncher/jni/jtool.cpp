#include <stdlib.h>
#include <string>
#include <list>
#include <jni.h>
using namespace std;


typedef struct {
	int id;
	string key;
} ITEM;

typedef struct {
	int key;
	int id;
} INTITEM;

#pragma pack(1)
typedef struct bmp_header
{
    char bfType[2];
    unsigned int bfSize;
    unsigned int bfReserved1;
    unsigned int bfOffBits;
    unsigned int biSize;
    int biWidth;
    int biHeight;
    unsigned short biPlanes;
    unsigned short biBitCount;
    unsigned int biCompression;
    unsigned int biSizeImage;
    int biXPelsPerMeter;
    int biYPelsPerMeter;
    unsigned int biClrUsed;
    unsigned int biClrImportant;
}BMPHEADER;

#pragma pack()

bool compare_nocase_greater(ITEM first, ITEM second) {

	unsigned int i = 0;
	while ((i < first.key.length()) && (i < second.key.length())) {
		if ((unsigned char)first.key[i] < (unsigned char)second.key[i])
			return true;
		else if ((unsigned char)first.key[i]>(unsigned char)second.key[i])
			return false;
		++i;
	}

	if (first.key.length() < second.key.length())
		return true;
	else
		return false;
}

bool compare_nocase_less(ITEM first, ITEM second) {

	unsigned int i = 0;
	while ((i < first.key.length()) && (i < second.key.length())) {
		if ((unsigned char)first.key[i] < (unsigned char)second.key[i])
			return false;
		else if ((unsigned char)first.key[i]>(unsigned char)second.key[i])
			return true;
		++i;
	}

	if (first.key.length() < second.key.length())
		return false;
	else
		return true;
}


void tolower(char* src){
	if(!src)
		return;
	do{
		if('A' <= *src && *src <='Z'){
			*src |= 0x20;
		}
	}while(*src++);
}

int MoreUTF8ToGB(char *utf8_ptr,int utf8_len,char *gb_ptr,int gb_len);
extern "C"
jint Java_com_iLoong_launcher_SetupMenu_cut_sort__I_3Ljava_lang_String_2_3I
	(JNIEnv* env, jclass cls, int sorttype,	jobjectArray keyarray, jintArray sortkeyarray)
{

	int size = env->GetArrayLength(keyarray);

	if ( !size )
		return -1;

	int i = 0;
	char src[256];
	list < ITEM > sortlist;

	for (i = 0; i < size; i++) {

		jstring jkey = (jstring) env->GetObjectArrayElement(keyarray, i);
		const char* ckey = env->GetStringUTFChars(jkey, NULL);
		memset(src, 0x00, sizeof(src));
		MoreUTF8ToGB((char*)ckey, strlen(ckey), src, sizeof(src)-1);
		tolower(src);

		ITEM item;
		item.id = i;
		item.key = (char*)src;
		sortlist.push_back(item);

		env->ReleaseStringUTFChars(jkey, ckey);
		env->DeleteLocalRef(jkey);
	}

	int* id = (int*) malloc(size * sizeof(int));
	if(sorttype)
		sortlist.sort(compare_nocase_greater);
	else
		sortlist.sort(compare_nocase_less);

	list < ITEM > :: iterator it;
	for(it = sortlist.begin(), i=0; it != sortlist.end(); it++, i++ ){
		id[i] = it->id;
	}
	env->SetIntArrayRegion(sortkeyarray, 0 , size, id);
	free(id);
	return 0;
}



int comp_int_less(const void *a,const void *b)
{
	return ((INTITEM*)a)->key - ((INTITEM*)b)->key;
}

int comp_int_great(const void *a,const void *b)
{
	return ((INTITEM*)b)->key - ((INTITEM*)a)->key;
}

extern "C"
jint Java_com_iLoong_launcher_SetupMenu_cut_sort__I_3I_3I
	(JNIEnv *env, jclass cls, jint sorttype, jintArray keyarray, jintArray sortkeyarray)
{

	int size = env->GetArrayLength(keyarray);

	if ( !size )
		return -1;
	int* key = (int*) malloc(size * sizeof(int));

	INTITEM* keyitem = (INTITEM*) malloc(size * sizeof(INTITEM));
	env->GetIntArrayRegion(keyarray, 0, size, key);

	int i=0;
	for (i = 0; i < size; i++) {
		keyitem[i].id = i;
		keyitem[i].key = key[i];
	}

	if(sorttype)
		qsort(keyitem, size, sizeof(INTITEM), comp_int_less);
	else
		qsort(keyitem, size, sizeof(INTITEM), comp_int_great);

	for (i = 0; i < size; i++) {
		key[i] = keyitem[i].id;
	}

	env->SetIntArrayRegion(sortkeyarray, 0, size, key);
	free(key);
	free(keyitem);
	return 0;

}


extern "C"
int Java_com_iLoong_launcher_SetupMenu_cut_bmp(JNIEnv* env, jclass cls,
		jint w, jint h, jintArray buffer, jbyteArray bmp) {

	BMPHEADER head;
    jint* rgb = env->GetIntArrayElements(buffer, NULL);
    if(rgb == NULL)
    	return 0;

    int size = w * h * 4;
    memset(&head, 0, sizeof(BMPHEADER));
    head.bfType[0] = 'B';
    head.bfType[1] = 'M';
    head.bfOffBits = sizeof(BMPHEADER);
    head.bfSize = head.bfOffBits + size;
    head.biSize = 40;
    head.biWidth = w;
    head.biHeight = -h;
    head.biPlanes = 1;
    head.biBitCount = 32;
    head.biCompression = 0;
    head.biSizeImage = size;

//    register unsigned char temp;
//    for(int i=0; i<size; i+=4){
//    	temp = rgb[i];
//    	rgb[i] = rgb[i+2];
//    	rgb[i+2] = temp;
//    }
    env->SetByteArrayRegion(bmp, 0, sizeof(head), (jbyte *)&head);
    env->SetByteArrayRegion(bmp, sizeof(head), size, (jbyte *)rgb);
    env->ReleaseIntArrayElements(buffer, rgb, 0);

    return (sizeof(head) + size);
}

