package com.iLoong.launcher.SetupMenu.Actions;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;

public class ShareAction extends Action {

	public ShareAction(int actionid, String action) {
		super(actionid, action);
		putIntentAction(SetupMenu.getContext(), ContactPicker.class);
	}

	public static void Init() {
		SetupMenuActions.getInstance().RegisterAction(ActionSetting.ACTION_SHARE,
				new ShareAction(ActionSetting.ACTION_SHARE, ShareAction.class.getName()));
	}

	@Override
	protected void OnRunAction() {
		SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		SynRunAction();
	}

	@Override
	protected void OnActionFinish() {

	}

	@Override
	protected void OnPutValue(String key) {

	}

	public static String getContactPhone(Cursor cursor) {

		int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		int phoneNum = cursor.getInt(phoneColumn);
		String phoneResult = "";
		if (phoneNum > 0) {
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
			Cursor phones = SetupMenu
					.getContext()
					.getContentResolver()
					.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

			if (phones.moveToFirst()) {
				for (; !phones.isAfterLast(); phones.moveToNext()) {
					int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//					int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
//					int phone_type = phones.getInt(typeindex);
					String phoneNumber = phones.getString(index);
					
					phoneResult = phoneNumber;
					
					if (!phoneResult.equals(""))/*选择第一个号码*/
						break;
//					switch (phone_type) {
//					case 2:
//						phoneResult = phoneNumber;
//						break;
//					}
				}
				if (!phones.isClosed()) {
					phones.close();
				}
			}
		}
		return phoneResult;
	}

	public static class ContactPicker extends Activity {

		@Override
		public void onCreate(Bundle icicle) {
			super.onCreate(icicle);
			Intent intent = new Intent(Intent.ACTION_PICK);//, ContactsContract.Contacts.CONTENT_URI);
			intent.setType("vnd.android.cursor.dir/contact");
			startActivityForResult(intent, ActionSetting.ACTION_PICK_FRIEND);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {

			super.onActivityResult(requestCode, resultCode, data);

//			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case (ActionSetting.ACTION_PICK_FRIEND):
					String phoneNum = "";
					if(data != null){
						Uri contactData = data.getData();
						Cursor c = managedQuery(contactData, null, null, null, null);
						c.moveToFirst();
						phoneNum = ShareAction.getContactPhone(c);
					}
					Uri smsToUri = Uri.parse("smsto:" + phoneNum);
					Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
					intent.putExtra("sms_body",
							SetupMenu.getContext().getResources().getString(RR.string.setting_share_friend));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					startActivity(intent);
					break;
				}
//			}
			finish();
		}
	}

}
