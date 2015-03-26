package com.cooee.launcher.layout.virtualicon;

import java.lang.reflect.Method;

/**
 * 
 * @author zhongqihong
 * **/
public class VirtualClick {

	public static class CallSpecification {

		/**
		 * describles the function you are looking for.
		 * 
		 * callSpec[0]: the class where function is placed.
		 * 
		 * 
		 **/
		String[] callSpec;

		/**
		 * the arguments passed to the function.
		 * **/
		Class[] argumentsTypes;

		/**
		 * the type of arguments for the function.
		 * **/
		Object[] arguments;
		/**
		 * 
		 * whether invoke the function immediately.
		 * 
		 * */

		boolean immediately;

	}

	// public static <T> T call(CallSpecification callSpec) {
	//
	// if (callSpec == null) {
	// throw new IllegalArgumentException(
	// "dude,you didnt give any useful argument at all.");
	// }
	//
	// try {
	// Class clazz = Class.forName(callSpec.callSpec[0]);
	// T t = (T) clazz.newInstance();
	//
	// if (callSpec.immediately) {
	// Method method = clazz.getDeclaredMethod(callSpec.callSpec[1],
	// callSpec.argumentsTypes);
	// Object obj = method.invoke(t, callSpec.arguments);
	// }
	//
	// return t;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }

	public static void call(CallSpecification callSpec) {

		if (callSpec == null) {
			throw new IllegalArgumentException(
					"dude,you didnt give any useful argument at all.");
		}

		try {
			Class clazz = Class.forName(callSpec.callSpec[0]);

			Method method = clazz.getDeclaredMethod(callSpec.callSpec[1],
					callSpec.argumentsTypes);
			Object obj = method.invoke(clazz.newInstance(), callSpec.arguments);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
